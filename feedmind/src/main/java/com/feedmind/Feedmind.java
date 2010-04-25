package com.feedmind;

//import com.sun.syndication.feed.synd.SyndContent;
//import com.sun.syndication.feed.synd.SyndEntry;
//import com.sun.syndication.feed.synd.SyndFeed;
//import com.sun.syndication.io.SyndFeedInput;
//import com.sun.syndication.io.XmlReader;
import com.toluju.util.Log;
//import edu.stanford.nlp.ling.HasWord;
//import edu.stanford.nlp.ling.Sentence;
//import edu.stanford.nlp.ling.TaggedWord;
//import edu.stanford.nlp.tagger.maxent.MaxentTagger;
import edu.mit.csail.brill.BrillTagger;
import edu.mit.csail.brill.Sentence;
import edu.mit.csail.brill.TaggedToken;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;

/**
 * @author Toby
 */
public class Feedmind {
  private static final Log log = new Log(Feedmind.class);

  protected static final int PAGE_LIMIT = 200;

  public static void main(String[] args) throws Exception {
    log.info("Loading tagger...");
    //MaxentTagger.init("models/left3words-wsj-0-18.tagger");
    log.info("Starting feed reader...");
    //URL url = new URL("http://www.google.com/reader/public/atom/user%2F10021756912915076970%2Fstate%2Fcom.google%2Fbroadcast");
    URL url = new URL("https://www.google.com/reader/shared/10021756912915076970");
    Feedmind fm = new Feedmind(url);
    fm.run();
    log.info("Done!");
  }

  //protected MaxentTagger tagger = new MaxentTagger();
  protected BrillTagger tagger = new BrillTagger();
  protected URL url;
  protected Map<String, Integer> words = new HashMap<String, Integer>();

  public Feedmind(URL url) {
    this.url = url;
    tagger.setup();
  }

  protected Pattern entryPattern = Pattern.compile(
    "<div class=\"item-body\">(.*?)</div>",
    Pattern.MULTILINE | Pattern.DOTALL);
  protected Pattern nextPattern = Pattern.compile(
    "<div id=\"more\"><a href=\"(.*?)\">.*?</a></div>",
    Pattern.MULTILINE | Pattern.DOTALL);

  public void run() {
    try {
      URL currentURL = url;

      for (int x = 0; x < PAGE_LIMIT; ++x) {
        if (x > 0 && x % 5 == 0) log.info("Step {0}", x);
        InputStream stream = currentURL.openStream();
        String rawHTML = IOUtils.toString(stream);
        stream.close();

        Matcher matcher = entryPattern.matcher(rawHTML);

        while (matcher.find())
          processEntry(matcher.group(1));
        
        matcher = nextPattern.matcher(rawHTML);
        
        if (!matcher.find()) {
          log.warn("Could not find url for next page!");
          break;
        }
        
        currentURL = new URL(matcher.group(1));
      }

//      SyndFeedInput input = new SyndFeedInput();
//      XmlReader reader = new XmlReader(url);
//      SyndFeed feed = input.build(reader);
//
//      for (SyndEntry entry : (List<SyndEntry>) feed.getEntries()) {
//        for (SyndContent content : (List<SyndContent>) entry.getContents()) {
//          processEntry(content.getValue());
//        }
//      }
//
//      reader.close();

      List<Map.Entry<String, Integer>> wordList =
        new ArrayList<Map.Entry<String, Integer>>();
      wordList.addAll(words.entrySet());
      Collections.sort(wordList, new Comparator<Map.Entry<String, Integer>>() {
        @Override
        public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
          return o2.getValue().compareTo(o1.getValue());
        }
      });

      int count = 0;
      for (Map.Entry<String, Integer> word : wordList) {
        System.out.println(word.getValue() + "\t" + word.getKey());

        if (++count > 50) {
          break;
        }
      }

      tagger.destroy();
    }
    catch (Exception e) {
      log.fatal("Exception processing feed", e);
    }
  }

  protected void processEntry(String content) {
    content = content.replace("<br>", "\n").replace("<br/>", "\n");
    content = StringEscapeUtils.unescapeHtml(content);
    content = StringEscapeUtils.unescapeXml(content);
    content = content.replace('’', '\'');
    content = content.replace('”', '"');
    content = content.replaceAll("<.*?>", "").trim();

    if (content.length() == 0) {
      return;
    }

    Sentence sentence = null;

    try {
      sentence = tagger.tag(content);
    }
    catch (Throwable t) {
      log.warn("Exception tagging content: {0}", content);
      log.warn("Detail", t);
      return;
    }

    Iterator<TaggedToken> it = sentence.getTokens();
    while (it.hasNext()) {
      processWord(it.next());
    }
  }

  protected void processWord(TaggedToken word) {
    if (StopWords.contains(word.getToken())) return;
    if (BlackList.contains(word.getToken())) return;

    //if (word.getTokenType().equals("NN") || word.getTokenType().equals("NNP") ||
    //    word.getTokenType().equals("NNS") || word.getTokenType().equals("NNPS")) {
    if (word.getTokenType().equals("NNP") || word.getTokenType().equals("NNPS")) {
      String wordString = word.getToken().toLowerCase();

      if (words.containsKey(wordString)) {
        int count = words.get(wordString);
        ++count;
        words.put(wordString, count);
      }
      else {
        words.put(wordString, 1);
      }
    }
  }

//  protected void processEntry(String content) {
//    content = content.replace("<br>", "\n").replace("<br/>", "\n");
//    content = StringEscapeUtils.unescapeHtml(content);
//    content = StringEscapeUtils.unescapeXml(content);
//    content = content.replaceAll("<.*?>", "");
//
//    StringReader reader = new StringReader(content);
//    List<Sentence<? extends HasWord>> sentences = MaxentTagger.tokenizeText(reader);
//
//    for (Sentence<? extends HasWord> sentence: sentences) {
//      Sentence<TaggedWord> taggedSentence = tagger.processSentence(sentence);
//
//      for (TaggedWord word : taggedSentence) {
//        processWord(word);
//      }
//    }
//  }
//
//  protected void processWord(TaggedWord word) {
//    if (StopWords.contains(word.word())) return;
//
//    if (word.tag().equals("NN") || word.tag().equals("NNP") ||
//        word.tag().equals("NNS") || word.tag().equals("NNPS")) {
//      String wordString = word.word().toLowerCase();
//
//      if (words.containsKey(wordString)) {
//        int count = words.get(wordString);
//        ++count;
//        words.put(wordString, count);
//      }
//      else {
//        words.put(wordString, 1);
//      }
//    }
//  }
}
