import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class InfiniteAndNaNAndNegativeDoubleArgsProvider extends InfiniteAndNaNDoubleArgsProvider {
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        return Stream.concat(
                super.provideArguments(context),
                Stream.of(Arguments.of(-0.1))
        );
    }
}
