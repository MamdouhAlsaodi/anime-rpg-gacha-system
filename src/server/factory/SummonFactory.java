package server.factory;

import server.model.enums.Rarity;
import java.util.ArrayList;
import java.util.List;

public interface SummonFactory<T> {
    T create(Rarity rarity);

    default List<T> createBatch(SummonFactory<T> factory, Rarity rarity, int count) {
        List<T> batch = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            batch.add(factory.create(rarity));
        }
        return batch;
    }
}
