(ns lucene-cascalog.core
  (:use cascalog.api)
  (:import
   org.apache.lucene.analysis.standard.StandardAnalyzer
   org.apache.lucene.analysis.TokenStream
   org.apache.lucene.util.Version
   org.apache.lucene.analysis.tokenattributes.TermAttribute))

(defn tokenizer-seq
  "Build a lazy-seq out of a tokenizer with TermAttribute"
  [^TokenStream tokenizer ^TermAttribute term-att]
  (lazy-seq
    (when (.incrementToken tokenizer)
      (cons (.term term-att) (tokenizer-seq tokenizer term-att)))))

(defn load-analyzer [^java.util.Set stopwords]
  (StandardAnalyzer. Version/LUCENE_CURRENT stopwords))

(defn tokenize-text
  "Apply a lucene tokenizer to cleaned text content as a lazy-seq"
  [^StandardAnalyzer analyzer page-text]
  (let [reader (java.io.StringReader. page-text)
        tokenizer (.tokenStream analyzer nil reader)
        term-att (.addAttribute tokenizer TermAttribute)]
    (tokenizer-seq tokenizer term-att)))

(defn emit-tokens [tokens-seq]
  "Compute n-grams of a seq of tokens"
  (partition 1 1 tokens-seq))

(defmapcatop tokenize-string {:stateful true}
  ([] (load-analyzer StandardAnalyzer/STOP_WORDS_SET))
  ([analyzer text]
     (emit-tokens (tokenize-text analyzer text)))
  ([analyzer] nil))

(defn tokenize-strings [in-path out-path]
  (let [src (hfs-textline in-path)]
    (?<- (hfs-textline out-path :sink-mode :replace)
         [!line ?token]
         (src !line)
         (tokenize-string !line :> ?token)
         (:distinct false))))

