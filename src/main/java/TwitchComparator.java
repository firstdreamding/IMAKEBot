import com.github.twitch4j.helix.domain.Stream;

import java.util.Comparator;

public class TwitchComparator implements Comparator<Stream> {
    @Override
    public int compare(Stream o1, Stream o2) {
        return o2.getViewerCount() - o1.getViewerCount();
    }
}
