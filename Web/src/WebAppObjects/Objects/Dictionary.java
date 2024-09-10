package WebAppObjects.Objects;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.HashSet;
import java.util.Set;

@XmlRootElement(name = "dictionary")
@XmlAccessorType(XmlAccessType.FIELD)
public class Dictionary {
    private String name;
    private final Set<String> dictionary;

    public Dictionary(String name, Set<String> dictionary) {
        this.name = name;
        this.dictionary = new HashSet<String>(dictionary);
    }

    public String getName() {
        return name;
    }

    public Set<String> getDictionary() {
        return dictionary;
    }

    public void setName(String name) {
        this.name = name;
    }

}
