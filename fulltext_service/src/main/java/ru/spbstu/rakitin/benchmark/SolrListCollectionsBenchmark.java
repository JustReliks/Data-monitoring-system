package ru.spbstu.rakitin.benchmark;

import org.apache.http.client.utils.HttpClientUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.SSLConfig;
import org.apache.solr.client.solrj.impl.CloudSolrClient;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.client.solrj.impl.HttpClientUtil;
import org.apache.solr.client.solrj.request.CollectionAdminRequest;
import ru.spbstu.rakitin.fulltext_service.engine.client.DockerHttp2SolrClient;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class SolrListCollectionsBenchmark {

    private static final String COLLECTION_PREFIX = "test_collection_";
    private static final String STATE_FILE = "solr_benchmark_state.properties";

//    public static void main(String[] args) {
//        try (SolrClient cloudClient = createCloudClient()) {
//            // Загружаем состояние предыдущего запуска
//            BenchmarkState state = loadState();
//
//            // Если есть незавершенный тест, предлагаем продолжить
//            if (state.lastTestedCount > 0 && !state.completed) {
//                System.out.printf("Обнаружен незавершенный тест (до %d коллекций). Продолжить? (y/n): ", state.lastTestedCount);
//                Scanner scanner = new Scanner(System.in);
//                if (scanner.nextLine().equalsIgnoreCase("y")) {
//                    state.completed = false; // Продолжаем с того же места
//                } else {
//                    state.reset(); // Начинаем заново
//                }
//            }
//
//            // Тестируем с разным количеством коллекций
//            int[] collectionCounts = {500, 1000, 1500}; // Количество коллекций для теста
//
//            for (int count : collectionCounts) {
//                if (state.completed && count <= state.lastTestedCount) {
//                    System.out.printf("Тест для %d коллекций уже выполнен. Пропускаем.%n", count);
//                    continue;
//                }
//
//                state.currentTargetCount = count;
//                saveState(state);
//
//                // Создаем коллекции (с проверкой существующих)
//                createTestCollections(cloudClient, count, state);
//
//                // Замеряем время выполнения запроса LIST
//                long duration = measureListCollectionsTime(cloudClient);
//
//                System.out.printf("Collections: %d, Time: %d ms%n", count, duration);
//
//                // Обновляем состояние
//                state.lastTestedCount = count;
//                state.completed = true;
//                saveState(state);
//
//                // Очищаем коллекции перед следующим тестом, если не последний
////                if (count != collectionCounts[collectionCounts.length - 1]) {
////                    clearAllCollections(cloudClient, state);
////                }
//            }
//
//            System.out.println("Все тесты завершены!");
//            state.reset();
//            saveState(state);
//
//        } catch (Exception e) {
//            System.err.println("Произошла ошибка: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
public static void main(String[] args) throws SolrServerException, IOException {
    String url = "https://localhost:8983/solr";

    Http2SolrClient client = new Http2SolrClient.Builder(url)
            .withSSLConfig(new VerifyHostsSslConfig(
                    true,
                    true,
                    "D:/programming/MDS-system/test/solr-ssl/keystore.p12",
                    "secret", "D:/programming/MDS-system/test/solr-ssl/truststore.p12", "secret",
                    false

            ))
            .useHttp1_1(true).build();
    List<String> collections = CollectionAdminRequest.listCollections(client);
    System.out.println(collections);

}

    private static SolrClient createCloudClient() {
        List<String> zk = Arrays.stream("localhost:2181,localhost:2182,localhost:2183".split(",")).toList();
        return new CloudSolrClient.Builder(zk, Optional.empty())
                .withHttpClient(dockerHttp2SolrClient()).build();
    }

    public static Http2SolrClient dockerHttp2SolrClient() {
        return new DockerHttp2SolrClient.Builder()
                .withBasicAuthCredentials("solr", "SolrRocks")
                .withRequestTimeout(10, TimeUnit.SECONDS)
                .withIdleTimeout(10, TimeUnit.MINUTES)
                .withConnectionTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    private static void createTestCollections(SolrClient client, int targetCount, BenchmarkState state) throws Exception {
        System.out.printf("Создаем %d тестовых коллекций...%n", targetCount);

        // Получаем список уже существующих коллекций
        Set<String> existingCollections = getExistingCollections(client);

        // Фильтруем только наши тестовые коллекции
        Set<String> ourCollections = new HashSet<>();
        for (String col : existingCollections) {
            if (col.startsWith(COLLECTION_PREFIX)) {
                ourCollections.add(col);
            }
        }

        System.out.printf("Найдено %d существующих тестовых коллекций%n", ourCollections.size());

        // Создаем только недостающие коллекции
        IntStream.range(0, targetCount).parallel().forEach(i -> {
            String collectionName = COLLECTION_PREFIX + i;
            if (!ourCollections.contains(collectionName)) {
                try {
                    System.out.printf("Создаем коллекцию %s%n", collectionName);
                    CollectionAdminRequest.createCollection(collectionName, 1, 1)
                            .process(client);

                    // Обновляем состояние после каждого успешного создания
                    synchronized (state) {
                        state.createdCollections.add(collectionName);
                        saveState(state);
                    }
                } catch (Exception e) {
                    System.err.printf("Ошибка при создании коллекции %s: %s%n",
                            collectionName, e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });

        System.out.println("Проверяем общее количество коллекций...");
        int actualCount = getExistingCollections(client).size();
        System.out.printf("Текущее количество коллекций: %d (цель: %d)%n", actualCount, targetCount);
    }

    private static Set<String> getExistingCollections(SolrClient client) throws Exception {
        List<String> listResponse = CollectionAdminRequest.listCollections(client);
        if (listResponse != null) {
            return new HashSet<>(listResponse);
        }
        return Collections.emptySet();
    }

    private static void clearAllCollections(SolrClient client, BenchmarkState state) throws Exception {
        System.out.println("Очищаем все тестовые коллекции...");
        Set<String> existingCollections = getExistingCollections(client);

        existingCollections.parallelStream()
                .filter(name -> name.startsWith(COLLECTION_PREFIX))
                .forEach(name -> {
                    try {
                        System.out.printf("Удаляем коллекцию %s%n", name);
                        CollectionAdminRequest.deleteCollection(name).process(client);

                        // Обновляем состояние после каждого удаления
                        synchronized (state) {
                            state.createdCollections.remove(name);
                            saveState(state);
                        }
                    } catch (Exception e) {
                        System.err.println("Ошибка при удалении коллекции " + name + ": " + e.getMessage());
                    }
                });

        System.out.println("Очистка завершена");
    }

    private static long measureListCollectionsTime(SolrClient client) throws Exception {
        int warmupRuns = 3;
        int measuredRuns = 5;

        // Разогрев
        for (int i = 0; i < warmupRuns; i++) {
            CollectionAdminRequest.listCollections(client);
        }

        // Измерение
        long totalTime = 0;
        for (int i = 0; i < measuredRuns; i++) {
            long startTime = System.currentTimeMillis();
            List<String> response = CollectionAdminRequest.listCollections(client);
            long endTime = System.currentTimeMillis();
            totalTime += (endTime - startTime);

            // Проверка, что ответ содержит коллекции
            if (response == null) {
                throw new RuntimeException("Invalid LIST response");
            }
        }

        return totalTime / measuredRuns; // Среднее время
    }

    private static BenchmarkState loadState() {
        Properties props = new Properties();
        try {
            props.load(SolrListCollectionsBenchmark.class.getClassLoader().getResourceAsStream(STATE_FILE));
        } catch (Exception e) {
            System.out.println("Не удалось загрузить состояние, начинаем заново");
            return new BenchmarkState();
        }

        BenchmarkState state = new BenchmarkState();
        state.lastTestedCount = Integer.parseInt(props.getProperty("lastTestedCount", "0"));
        state.completed = Boolean.parseBoolean(props.getProperty("completed", "true"));
        state.currentTargetCount = Integer.parseInt(props.getProperty("currentTargetCount", "0"));

        String collections = props.getProperty("createdCollections", "");
        if (!collections.isEmpty()) {
            state.createdCollections.addAll(Arrays.asList(collections.split(",")));
        }

        return state;
    }

    private static void saveState(BenchmarkState state) {
        Properties props = new Properties();
        props.setProperty("lastTestedCount", String.valueOf(state.lastTestedCount));
        props.setProperty("completed", String.valueOf(state.completed));
        props.setProperty("currentTargetCount", String.valueOf(state.currentTargetCount));
        props.setProperty("createdCollections", String.join(",", state.createdCollections));

        try {
            props.store(new java.io.FileOutputStream(STATE_FILE), "Solr Benchmark State");
        } catch (Exception e) {
            System.err.println("Не удалось сохранить состояние: " + e.getMessage());
        }
    }

    static class BenchmarkState {
        int lastTestedCount = 0;
        boolean completed = true;
        int currentTargetCount = 0;
        Set<String> createdCollections = ConcurrentHashMap.newKeySet();

        void reset() {
            lastTestedCount = 0;
            completed = true;
            currentTargetCount = 0;
            createdCollections.clear();
        }
    }
}