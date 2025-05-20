package com.worldline.mts.idm.scimctl.commands.import_cmd;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CustomSpliterator<T> implements Spliterator<T> {
  private final List<T> nodes;
  private int index;

  // Méthode pour créer un Stream à partir de ce Spliterator
  public Stream<T> stream() {
    return StreamSupport.stream(this, false);
  }

  // Méthode pour créer un Stream parallèle
  public Stream<T> parallelStream() {
    return StreamSupport.stream(this, true);
  }


  public CustomSpliterator(List<T> nodes) {
    this.nodes = nodes;
    this.index = 0;
  }

  @Override
  public boolean tryAdvance(Consumer<? super T> action) {
    if (index < nodes.size()) {
      action.accept(nodes.get(index));
      index++;
      return true;
    }
    return false;
  }

  @Override
  public CustomSpliterator<T> trySplit() {
    if (estimateSize() <= 50) {
      return new CustomSpliterator(nodes.subList(index, nodes.size()));
    } else {
      int split = index + 50;
      var newSlpiterator = new CustomSpliterator(nodes.subList(split, nodes.size()));
      index = split;
      return newSlpiterator;
    }
  }

  @Override
  public long estimateSize() {
    return nodes.size() - index;
  }

  @Override
  public int characteristics() {
    return ORDERED | SIZED | SUBSIZED | NONNULL;
  }
}
