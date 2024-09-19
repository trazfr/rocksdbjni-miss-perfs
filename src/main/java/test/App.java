package test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

public final class App implements AutoCloseable {

    private record Key(int i, byte[] b) {
        Key(int i) {
            this(i, String.format("%08d", i).getBytes());
        }

        static List<Key> generateKeys(int from, int step, int num_keys) {
            var keys = new ArrayList<Key>(num_keys);
            for (int i = 0, key_num = from; i < num_keys; i++, key_num += step) {
                keys.add(new Key(key_num));
            }
            return keys;
        }
    }

    private static final String DB_PATH = "/tmp/rocksdb";
    // 1M keys in DB, 1M cache misses
    private static final int NUM_KEYS = 1_000_000;

    private final List<Key> keysInDb;
    private final List<Key> keysNotInDB;
    private final RocksDB db;

    private App() throws RocksDBException {
        keysInDb = Key.generateKeys(0, 2, NUM_KEYS);
        keysNotInDB = Key.generateKeys(1, 2, NUM_KEYS);
        db = RocksDB.open(DB_PATH);
        if (db.get(keysInDb.get(keysInDb.size() - 1).b()) == null) {
            fillDatabase();
        }
    }

    public static void main(String[] args) throws Exception {
        try (var app = new App()) {
            while (true) {
                app.readDatabaseHits();
                app.readDatabaseMisses();
            }
        }
    }

    private void fillDatabase() throws RocksDBException {
        var random = ThreadLocalRandom.current();
        var value = new byte[1024];
        System.out.printf("Fill the database with %d keys%n", keysInDb.size());

        for (var key : keysInDb) {
            random.nextBytes(value);
            db.put(key.b(), value);
        }
    }

    private void readDatabaseHits() throws RocksDBException {
        System.out.printf("Read the database with %d keys that are in the database%n", keysInDb.size());
        readDatabase(db, keysInDb);
    }

    private void readDatabaseMisses() throws RocksDBException {
        System.out.printf("Query the database with %d keys that are NOT in the database%n", keysNotInDB.size());
        readDatabase(db, keysNotInDB);
    }

    private static void readDatabase(RocksDB db, List<Key> keys) throws RocksDBException {
        var start = System.nanoTime();
        for (var key : keys) {
            db.get(key.b());
        }
        var end = System.nanoTime();
        System.out.printf("Time: %fms%n", (end - start) / 1_000_000.);
    }

    @Override
    public void close() throws RocksDBException {
        db.close();
    }
}
