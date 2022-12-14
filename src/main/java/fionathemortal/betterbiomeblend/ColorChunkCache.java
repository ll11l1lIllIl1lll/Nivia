package fionathemortal.betterbiomeblend;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;

import java.util.Arrays;
import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class ColorChunkCache
{
    public final Lock lock;

    public final Long2ObjectLinkedOpenHashMap<ColorChunk> hash;
    public final Stack<ColorChunk>                        freeStack;

    public int invalidationCounter;

    public static long
    getChunkKey(int chunkX, int chunkZ, int colorType)
    {
        long result =
            ((long)(chunkZ & 0x03FFFFFFL) << 26) |
            ((long)(chunkX & 0x03FFFFFFL))       |
            ((long)colorType << 52);

        return result;
    }

    public
    ColorChunkCache(int count)
    {
        lock = new ReentrantLock();

        hash      = new Long2ObjectLinkedOpenHashMap<ColorChunk>(count);
        freeStack = new Stack<ColorChunk>();

        for (int index = 0;
            index < count;
            ++index)
        {
            freeStack.add(new ColorChunk());
        }
    }

    public void
    releaseChunkWithoutLock(ColorChunk chunk)
    {
        int refCount = chunk.release();

        if (refCount == 0)
        {
            freeStack.push(chunk);
        }
    }

    public void
    releaseChunk(ColorChunk chunk)
    {
        int refCount = chunk.release();

        if (refCount == 0)
        {
            lock.lock();

            freeStack.push(chunk);

            lock.unlock();
        }
    }

    public void
    invalidateNeighbourhood(int chunkX, int chunkZ)
    {
        lock.lock();

        ++invalidationCounter;

        for (int x = -1;
            x <= 1;
            ++x)
        {
            for (int z = -1;
                z <= 1;
                ++z)
            {
                for (int colorType = BiomeColorType.FIRST;
                    colorType < ColorResolverCompatibility.nextColorID;
                    ++colorType)
                {
                    long key = getChunkKey(chunkX + x, chunkZ + z, colorType);

                    ColorChunk chunk = hash.remove(key);

                    if (chunk != null)
                    {
                        releaseChunkWithoutLock(chunk);

                        chunk.markAsInvalid();
                    }
                }
            }
        }

        lock.unlock();
    }

    public void
    invalidateAll()
    {
        lock.lock();

        ++invalidationCounter;

        for (ColorChunk chunk : hash.values())
        {
            releaseChunkWithoutLock(chunk);

            chunk.markAsInvalid();
        }

        hash.clear();

        lock.unlock();
    }

    public ColorChunk
    getChunk(int chunkX, int chunkZ, int colorType)
    {
        ColorChunk result;

        long key = getChunkKey(chunkX, chunkZ, colorType);

        lock.lock();

        result = hash.getAndMoveToFirst(key);

        if (result != null)
        {
            result.acquire();
        }

        lock.unlock();

        return result;
    }

    public void
    putChunk(ColorChunk chunk)
    {
        chunk.acquire();

        lock.lock();

        ColorChunk prev = hash.getAndMoveToFirst(chunk.key);

        if (prev != null)
        {
            ColorChunk olderChunk;

            if (chunk.invalidationCounter >= prev.invalidationCounter)
            {
                olderChunk = prev;

                hash.put(chunk.key, chunk);
            }
            else
            {
                olderChunk = chunk;
            }

            releaseChunkWithoutLock(olderChunk);

            olderChunk.markAsInvalid();
        }
        else
        {
            hash.putAndMoveToFirst(chunk.key, chunk);
        }

        lock.unlock();
    }

    public ColorChunk
    newChunk(int chunkX, int chunkZ, int colorType)
    {
        ColorChunk result = null;

        long key = getChunkKey(chunkX, chunkZ, colorType);

        lock.lock();

        if (!freeStack.empty())
        {
            result = freeStack.pop();
        }
        else
        {
            for (;;)
            {
                long lastKey = hash.lastLongKey();

                result = hash.removeLast();

                if (result.getReferenceCount() == 1)
                {
                    result.release();
                    break;
                }
                else
                {
                    hash.putAndMoveToFirst(lastKey, result);
                }
            }
        }

        result.invalidationCounter = invalidationCounter;

        lock.unlock();

        result.key = key;
        result.acquire();

        return result;
    }

    public ColorChunk
    getOrDefaultInitializeChunk(int chunkX, int chunkZ, int colorType)
    {
        ColorChunk result;

        long key = getChunkKey(chunkX, chunkZ, colorType);

        lock.lock();

        result = hash.getAndMoveToFirst(key);

        if (result == null)
        {
            if (!freeStack.empty())
            {
                result = freeStack.pop();
            }
            else
            {
                for (;;)
                {
                    long lastKey = hash.lastLongKey();

                    result = hash.removeLast();

                    if (result.getReferenceCount() == 1)
                    {
                        result.release();
                        break;
                    }
                    else
                    {
                        hash.putAndMoveToFirst(lastKey, result);
                    }
                }
            }

            result.key = key;
            result.invalidationCounter = invalidationCounter;

            Arrays.fill(result.data, (byte)-1);

            result.acquire();

            ColorChunk prev = hash.putAndMoveToFirst(result.key, result);

            if (prev != null)
            {
                releaseChunkWithoutLock(prev);

                prev.markAsInvalid();
            }
        }

        result.acquire();

        lock.unlock();

        return result;
    }
}
