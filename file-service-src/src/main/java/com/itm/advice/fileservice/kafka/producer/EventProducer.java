package com.itm.advice.fileservice.kafka.producer;

/**
 * Публикатор событий
 *
 * @param <T> тип события
 */
public interface EventProducer<T> {

    void produce(T event);
}
