import java.util.Comparator;

public class RankComparator implements Comparator<CollegeRank> {
    @Override
    public int compare(CollegeRank o1, CollegeRank o2) {
        return o2.score - o1.score;
    }
}
