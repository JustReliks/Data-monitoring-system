package ru.spbstu.rakitin.commonstarter.sequence.engine;

import java.util.Map;

public interface SequentialTask {

   void perform(Map<String, Object> context) throws Exception;
   void rollback(Map<String, Object> context) throws Exception;

}
