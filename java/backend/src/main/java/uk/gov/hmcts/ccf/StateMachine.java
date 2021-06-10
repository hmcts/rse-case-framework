package uk.gov.hmcts.ccf;

import static org.jooq.generated.Tables.EVENTS;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.jodah.typetools.TypeResolver;
import org.jooq.InsertSetStep;
import org.jooq.InsertValuesStepN;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.generated.tables.Events;
import org.jooq.generated.tables.records.EventsRecord;
import org.jooq.impl.DefaultDSLContext;
import org.jooq.impl.TableImpl;
import org.jooq.impl.TableRecordImpl;
import uk.gov.hmcts.ccd.domain.model.aggregated.CaseUpdateViewEvent;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

public class StateMachine<StateT, EventT, R extends Record> {

    private final Table<R> table;
//    private final Function<InsertSetStep<R>, InsertValuesStepN<R>> binder;
    private StateT state;
    private Multimap<String, TransitionRecord> transitions = HashMultimap.create();
    private Collection<TransitionRecord> universalEvents = Lists.newArrayList();
    private Map<EventT, EventBuilder> events = Maps.newHashMap();
    private Map<EventT, TransitionRecord> dynamicEvents = Maps.newHashMap();
    private Class clazz;
    private BiConsumer initialHandler;
    private DefaultDSLContext jooq;

    private StateT initialState;

    public StateMachine(DefaultDSLContext jooq,
                        Table<R> table
//                        ,Function<InsertSetStep<R>, InsertValuesStepN<R>> binder
    ) {
        this.jooq = jooq;
        this.table = table;
//        this.binder = binder;
    }

    public CaseUpdateViewEvent getEvent(Long caseId, EventT event) {
        if (dynamicEvents.containsKey(event)) {
            TransitionRecord e = dynamicEvents.get(event);
            EventBuilder builder = new EventBuilder(e.clazz, event.toString(), event.toString());
            e.consumer.accept(caseId, builder);
            return builder.build();
        }
        if (events.containsKey(event)) {
            return events.get(event).build();
        }
        throw new RuntimeException("Unknown event: " + event);
    }

    public <T> StateMachine<StateT, EventT, R> initialState(StateT state, BiConsumer<TransitionContext, T> c) {
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

    @SneakyThrows
    public void handleEvent(TransitionContext context, EventT event, JsonNode data) {
        ObjectMapper o = new ObjectMapper()
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());
        for (TransitionRecord transitionRecord : transitions.get(state.toString())) {
            if (transitionRecord.event.equals(event)) {
                Object instance = o.treeToValue(data, transitionRecord.clazz);
                transitionRecord.consumer.accept(context, instance);
                state = transitionRecord.destination;
                return;
            }
        }

        for (TransitionRecord universalEvent : universalEvents) {
            if (universalEvent.event.equals(event)) {
                Object instance = o.treeToValue(data, universalEvent.clazz);
                universalEvent.consumer.accept(context, instance);
                return;
            }
        }
        throw new RuntimeException("Unhandled event:" + event);
    }

    private <T extends TableRecordImpl<T>> void onEvent() {
      throw new RuntimeException();
    }

    public StateT getState() {
        return state;
    }

    public <T> StateMachine<StateT, EventT, R> addUniversalEvent(EventT event, BiConsumer<TransitionContext, T> consumer) {
        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(BiConsumer.class, consumer.getClass());
        universalEvents.add(new TransitionRecord(null, event, typeArgs[1], consumer));
        return this;
    }

    public <T> EventBuilder<T> addTransition(StateT from, StateT to, EventT event,
                                             BiConsumer<TransitionContext, T> consumer) {
        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(BiConsumer.class, consumer.getClass());
        EventBuilder<T> result = new EventBuilder<T>((Class<T>) typeArgs[1], event.toString(), event.toString());
        transitions.put(from.toString(), new TransitionRecord(to, event, typeArgs[1], consumer));
        result.name("Confirm service");
        events.put(event, result);
        return result;
    }

    @SuppressWarnings("unchecked")
    public <T> EventBuilder<T> addEvent(StateT state, EventT event,
                                                     BiConsumer<TransitionContext, T> consumer) {
        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(BiConsumer.class, consumer.getClass());
        EventBuilder<T> result = new EventBuilder<T>((Class<T>) typeArgs[1], event.toString(), event.toString());
        transitions.put(state.toString(), new TransitionRecord(state, event, typeArgs[1], consumer));
        events.put(event, result);
        return result;
    }

    public <T> StateMachine<StateT, EventT, R> dynamicEvent(StateT state, EventT event,
                                                         BiConsumer<TransitionContext, T> consumer,
                                                         BiConsumer<Long, EventBuilder<T>> builder) {
        Class<?>[] typeArgs = TypeResolver.resolveRawArguments(BiConsumer.class, consumer.getClass());
        dynamicEvents.put(event, new TransitionRecord(state, event, typeArgs[1], builder));
        transitions.put(state.toString(), new TransitionRecord(state, event, typeArgs[1], consumer));
        return this;
    }

    public Set<EventT> getAvailableActions(StateT state) {
        return getAvailableActions(state.toString());
    }

    public Set<EventT> getAvailableActions(String state) {
        Set<EventT> result = Sets.newHashSet();
        for (TransitionRecord transitionRecord : transitions.get(state)) {
            result.add(transitionRecord.getEvent());
        }
        for (TransitionRecord universalEvent : universalEvents) {
            result.add(universalEvent.getEvent());
        }

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
