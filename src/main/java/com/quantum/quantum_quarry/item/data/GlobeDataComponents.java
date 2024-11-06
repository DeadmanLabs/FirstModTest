

public class GlobeDataComponents {
    public static final DataComponentType<Set<ResourceLocation>> VISITED_BIOMES = DataComponentType.register(
        new ResourceLocation("quantum", "visited_biomes"),
        Codec.unboundedMap(ResourceLocation.CODEC, Codec.BOOL).xmap(
            map -> map.keySet(),
            set -> set.stream().collect(Collectors.toMap(Function.identify(), v -> true))
        )
    );
}