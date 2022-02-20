import java.util.Comparator;

public class KDAComparator implements Comparator<KDA> {
    @Override
    public int compare(KDA o1, KDA o2) {
        return (int) ((o2.KDA - o1.KDA) * 10);
    }
}
