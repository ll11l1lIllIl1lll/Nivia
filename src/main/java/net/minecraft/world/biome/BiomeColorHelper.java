package net.minecraft.world.biome;

import fionathemortal.betterbiomeblend.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BiomeColorHelper
{
    public static final BiomeColorHelper.ColorResolver GRASS_COLOR = new BiomeColorHelper.ColorResolver()
    {
        public int getColorAtPos(Biome biome, BlockPos blockPosition)
        {
            return biome.getGrassColorAtPos(blockPosition);
        }
    };
    public static final BiomeColorHelper.ColorResolver FOLIAGE_COLOR = new BiomeColorHelper.ColorResolver()
    {
        public int getColorAtPos(Biome biome, BlockPos blockPosition)
        {
            return biome.getFoliageColorAtPos(blockPosition);
        }
    };
    public static final BiomeColorHelper.ColorResolver WATER_COLOR = new BiomeColorHelper.ColorResolver()
    {
        public int getColorAtPos(Biome biome, BlockPos blockPosition)
        {
            return biome.getWaterColor();
        }
    };

    private static int getColorAtPos(IBlockAccess blockAccess, BlockPos pos, BiomeColorHelper.ColorResolver colorResolver)
    {
        int x = pos.getX();
        int z = pos.getZ();

        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        int colorResolverID = ColorResolverCompatibility.getColorResolverID(colorResolver);

        ThreadLocal<ColorChunk> threadLocal = BiomeColor.getThreadLocalGenericChunkWrapper(blockAccess);

        ColorChunk chunk = BiomeColor.getThreadLocalChunk(threadLocal, chunkX, chunkZ, colorResolverID);

        if (chunk == null)
        {
            ColorChunkCache cache = BiomeColor.getColorChunkCacheForIBlockAccess(blockAccess);

            chunk = BiomeColor.getBlendedColorChunk(cache, blockAccess, colorResolverID, chunkX, chunkZ, colorResolver);

            BiomeColor.setThreadLocalChunk(threadLocal, chunk, cache);
        }

        int result = chunk.getColor(x, z);

        return result;
    }

    public static int getGrassColorAtPos(IBlockAccess blockAccess, BlockPos pos)
    {
        int x = pos.getX();
        int z = pos.getZ();

        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        ThreadLocal<ColorChunk> threadLocal = BiomeColor.getThreadLocalGrassChunkWrapper(blockAccess);

        ColorChunk chunk = BiomeColor.getThreadLocalChunk(threadLocal, chunkX, chunkZ, BiomeColorType.GRASS);

        if (chunk == null)
        {
            ColorChunkCache cache = BiomeColor.getColorChunkCacheForIBlockAccess(blockAccess);

            chunk = BiomeColor.getBlendedColorChunk(cache, blockAccess, BiomeColorType.GRASS, chunkX, chunkZ, BiomeColorHelper.GRASS_COLOR);

            BiomeColor.setThreadLocalChunk(threadLocal, chunk, cache);
        }

        int result = chunk.getColor(x, z);

        return result;
    }

    public static int getFoliageColorAtPos(IBlockAccess blockAccess, BlockPos pos)
    {
        int x = pos.getX();
        int z = pos.getZ();

        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        ThreadLocal<ColorChunk> threadLocal = BiomeColor.getThreadLocalFoliageChunkWrapper(blockAccess);

        ColorChunk chunk = BiomeColor.getThreadLocalChunk(threadLocal, chunkX, chunkZ, BiomeColorType.FOLIAGE);

        if (chunk == null)
        {
            ColorChunkCache cache = BiomeColor.getColorChunkCacheForIBlockAccess(blockAccess);

            chunk = BiomeColor.getBlendedColorChunk(cache, blockAccess, BiomeColorType.FOLIAGE, chunkX, chunkZ, BiomeColorHelper.FOLIAGE_COLOR);

            BiomeColor.setThreadLocalChunk(threadLocal, chunk, cache);
        }

        int result = chunk.getColor(x, z);

        return result;
    }

    public static int getWaterColorAtPos(IBlockAccess blockAccess, BlockPos pos)
    {
        int x = pos.getX();
        int z = pos.getZ();

        int chunkX = x >> 4;
        int chunkZ = z >> 4;

        ThreadLocal<ColorChunk> threadLocal = BiomeColor.getThreadLocalWaterChunkWrapper(blockAccess);

        ColorChunk chunk = BiomeColor.getThreadLocalChunk(threadLocal, chunkX, chunkZ, BiomeColorType.WATER);

        if (chunk == null)
        {
            ColorChunkCache cache = BiomeColor.getColorChunkCacheForIBlockAccess(blockAccess);

            chunk = BiomeColor.getBlendedColorChunk(cache, blockAccess, BiomeColorType.WATER, chunkX, chunkZ, BiomeColorHelper.WATER_COLOR);

            BiomeColor.setThreadLocalChunk(threadLocal, chunk, cache);
        }

        int result = chunk.getColor(x, z);

        return result;
    }

    public interface ColorResolver
    {
        int getColorAtPos(Biome biome, BlockPos blockPosition);
    }
}
