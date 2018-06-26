## Simple Extractive Summarisation

Extractive summarization of text - simple algorithm that scores texts based on position in story, numbers, proper nouns,
thematic relationships between title and sentences, cosine relationships between sentences, and themes based on frequencies.

## Build and test on Ubuntu 16.04 or 18.04
```
gradle clean build
cd dist
# summarize Romeo and Juliet in 10 sentences
./summarize.sh ../resources/test/romeo_and_juliet.txt 10
```
