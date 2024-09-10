package WebAppObjects.Managers;

import WebAppObjects.Objects.Dictionary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DictionariesManager {
    final Set<Dictionary> dictionaries;
    public DictionariesManager() {
        dictionaries = new HashSet<Dictionary>();
    }

    public synchronized void addDictionary(Dictionary dictionary) {
        dictionaries.add(dictionary);
    }

    public synchronized void removeDictionary(Dictionary dictionary) {
        dictionaries.remove(dictionary);
    }

    public synchronized final List<Dictionary> getDictionaries() {
        return new ArrayList<Dictionary>(dictionaries);
    }

    public synchronized final Dictionary getDictionaryByName(String name) {
        for (Dictionary dictionary : dictionaries) {
            if (dictionary.getName().equals(name)) {
                return dictionary;
            }
        }
        return null;
    }





}
