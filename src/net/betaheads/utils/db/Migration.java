package net.betaheads.utils.db;

public interface Migration {
  String getName();
  void run();
}
