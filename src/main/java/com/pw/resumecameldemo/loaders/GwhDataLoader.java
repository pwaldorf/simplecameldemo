package com.pw.resumecameldemo.loaders;

public interface GwhDataLoader<T> {

    T load(String formatName);

}
