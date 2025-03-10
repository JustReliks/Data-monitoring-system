package ru.spbstu.rakitin.commonstarter.sequence.engine;

import java.util.Map;

public interface SequentialTask {

   void perform(Map<String, String> context) throws Exception;
   void rollback(Map<String, String> context) throws Exception;

}
