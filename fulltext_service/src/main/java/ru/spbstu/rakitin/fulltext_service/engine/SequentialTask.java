package ru.spbstu.rakitin.fulltext_service.engine;

public interface SequentialTask {

   void perform() throws Exception;
   void rollback() throws Exception;

}
