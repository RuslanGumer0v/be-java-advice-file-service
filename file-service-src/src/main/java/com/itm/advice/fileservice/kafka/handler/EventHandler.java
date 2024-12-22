package com.itm.advice.fileservice.kafka.handler;

/**
 * Обработчик событий
 *
 * @param <T> тип события
 */
public interface EventHandler<T> {

    /**
     * Метод проверяет возможность обработки данного события конкретным обработчиком
     *
     * @param event объект события
     * @return true - если данный обработчик может обработать данное событие, false - если нет
     */
    boolean isHandle(T event);

    /**
     * Обработать событие
     *
     * @param event объект события
     */
    void handle(T event);
}
