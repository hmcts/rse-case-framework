package uk.gov.hmcts.ccf;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.jodah.typetools.TypeResolver;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;

public class StateMachine<StateT, EventT> {

    private StateT state;
    private Multimap<String, Transition> transitions = HashMultimap.create();
    private Collection<Transition> universalEvents = Lists.newArrayList();
    private Table<String, String, BiConsumer<Long, MultipartFile>> uploadHandlers = HashBasedTable.create();

    private Class clazz;
    private BiConsumer initialHandler;
    private StateT initialState;

    public StateMachine() {
    }

    public <T> StateMachine<StateT, EventT> initialState(StateT state, BiConsumer<Long, T> c) {
        this.initialState = state;
        this.state = initialState;
        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(BiConsumer.class, c.getClass());
        this.clazz = typeArgs[1];
        this.initialHandler = c;
        return this;
    }

    @SneakyThrows
    public void onCreated(Long caseId, JsonNode data) {
        Object instance = new ObjectMapper().treeToValue(data, clazz);
        initialHandler.accept(caseId, instance);
        state = initialState;
    }

    public void handleFileUpload(String state, Long caseId, EventT event, MultipartFile file) {
        BiConsumer<Long, MultipartFile> handler = this.uploadHandlers.get(state, event.toString());
        if (handler != null) {
            handler.accept(caseId, file);
        } else {
            throw new RuntimeException("No file upload handler for " + event);
        }
    }

    @SneakyThrows
    public void handleEvent(Long caseId, EventT event, JsonNode data) {
        for (Transition transition : transitions.get(state.toString())) {
            if (transition.event.equals(event)) {
                Object instance = new ObjectMapper().treeToValue(data, transition.clazz);
                transition.consumer.accept(caseId, instance);
                state = transition.destination;
                return;
            }
        }

        for (Transition universalEvent : universalEvents) {
            if (universalEvent.event.equals(event)) {
                Object instance = new ObjectMapper().treeToValue(data, universalEvent.clazz);
                universalEvent.consumer.accept(caseId, instance);
                return;
            }
        }
    }

    public StateT getState() {
        return state;
    }

    public <T> StateMachine<StateT, EventT> addUniversalEvent(EventT event, BiConsumer<Long, T> consumer) {
        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(BiConsumer.class, consumer.getClass());
        universalEvents.add(new Transition(null, event, typeArgs[1], consumer));
        return this;
    }

    public <T> StateMachine<StateT, EventT> addTransition(StateT from, StateT to, EventT event,
                                                          BiConsumer<Long, T> consumer) {
        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(BiConsumer.class, consumer.getClass());
        transitions.put(from.toString(), new Transition(to, event, typeArgs[1], consumer));
        return this;
    }

    public <T> StateMachine<StateT, EventT> addEvent(StateT state, EventT event, BiConsumer<Long, T> consumer) {
        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(BiConsumer.class, consumer.getClass());
        transitions.put(state.toString(), new Transition(state, event, typeArgs[1], consumer));
        return this;
    }

    public <T> StateMachine<StateT, EventT> addFileUploadEvent(StateT state, EventT event,
                                                               BiConsumer<Long, MultipartFile> consumer) {
        uploadHandlers.put(state.toString(), event.toString(), consumer);
        return this;
    }

    public Set<String> getAvailableActions(StateT state) {
        return getAvailableActions(state.toString());
    }

    public Set<String> getAvailableActions(String state) {
        Set<String> result = Sets.newHashSet();
        for (Transition transition : transitions.get(state)) {
            result.add(transition.getEvent().toString());
        }
        for (Transition universalEvent : universalEvents) {
            result.add(universalEvent.getEvent().toString());
        }

        result.addAll(this.uploadHandlers.columnKeySet());

        return result;
    }

    public void rehydrate(String state) {
        // TODO
        this.state = (StateT) Enum.valueOf(uk.gov.hmcts.unspec.enums.State.class, state);
    }

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    private class Transition {
        private final StateT destination;
        private final EventT event;
        private Class clazz;
        private BiConsumer consumer;
    }
}
