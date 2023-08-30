package com.acadiasoft.unixphilosophy;

import java.io.IOException;

public class WordCountRunner {
    public static void main(String[] args) {
        WordCount wordCount = new WordCount();
        wordCount.run(System.in);
    }
}
