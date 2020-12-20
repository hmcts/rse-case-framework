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
import lombok.Builder;
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
    private Multimap<String, TransitionRecord> transitions = HashMultimap.create();
    private Collection<TransitionRecord> universalEvents = Lists.newArrayList();
    private Table<String, String, BiConsumer<Long, MultipartFile>> uploadHandlers = HashBasedTable.create();

    private Class clazz;
    private BiConsumer initialHandler;

    private StateT initialState;

    public StateMachine() {
    }

    public <T> StateMachine<StateT, EventT> initialState(StateT state, BiConsumer<TransitionContext, T> c) {
        this.initialState = state;
        this.state = initialState;
        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(BiConsumer.class, c.getClass());
        this.clazz = typeArgs[1];
        this.initialHandler = c;
        return this;
    }

    @SneakyThrows
    public void onCreated(String userId, Long caseId, JsonNode data) {
        Object instance = new ObjectMapper().treeToValue(data, clazz);
        initialHandler.accept(new TransitionContext(userId, caseId), instance);
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
    public void handleEvent(TransitionContext context, EventT event, JsonNode data) {
        for (TransitionRecord transitionRecord : transitions.get(state.toString())) {
            if (transitionRecord.event.equals(event)) {
                Object instance = new ObjectMapper().treeToValue(data, transitionRecord.clazz);
                transitionRecord.consumer.accept(context, instance);
                state = transitionRecord.destination;
                return;
            }
        }

        for (TransitionRecord universalEvent : universalEvents) {
            if (universalEvent.event.equals(event)) {
                Object instance = new ObjectMapper().treeToValue(data, universalEvent.clazz);
                universalEvent.consumer.accept(context, instance);
                return;
            }
        }
    }

    public StateT getState() {
        return state;
    }

    public <T> StateMachine<StateT, EventT> addUniversalEvent(EventT event, BiConsumer<TransitionContext, T> consumer) {
        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(BiConsumer.class, consumer.getClass());
        universalEvents.add(new TransitionRecord(null, event, typeArgs[1], consumer));
        return this;
    }

    public <T> StateMachine<StateT, EventT> addTransition(StateT from, StateT to, EventT event,
                                                          BiConsumer<TransitionContext, T> consumer) {
        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(BiConsumer.class, consumer.getClass());
        transitions.put(from.toString(), new TransitionRecord(to, event, typeArgs[1], consumer));
        return this;
    }

    public <T> StateMachine<StateT, EventT> addEvent(StateT state, EventT event,
                                                     BiConsumer<TransitionContext, T> consumer) {
        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(BiConsumer.class, consumer.getClass());
        transitions.put(state.toString(), new TransitionRecord(state, event, typeArgs[1], consumer));
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
        for (TransitionRecord transitionRecord : transitions.get(state)) {
            result.add(transitionRecord.getEvent().toString());
        }
        for (TransitionRecord universalEvent : universalEvents) {
            result.add(universalEvent.getEvent().toString());
        }

        result.addAll(this.uploadHandlers.columnKeySet());

        return result;
    }

    public void rehydrate(StateT state) {
        this.state = state;
    }

    @Data
    @AllArgsConstructor
    @RequiredArgsConstructor
    private class TransitionRecord {
        private final StateT destination;
        private final EventT event;
        private Class clazz;
        private BiConsumer consumer;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class TransitionContext {
        private String userId;
        private Long entityId;
    }
}
