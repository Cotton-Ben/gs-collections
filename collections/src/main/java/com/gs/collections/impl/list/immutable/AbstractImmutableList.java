/*
 * Copyright 2014 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gs.collections.impl.list.immutable;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.RandomAccess;

import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.function.Function0;
import com.gs.collections.api.block.function.Function2;
import com.gs.collections.api.block.function.primitive.BooleanFunction;
import com.gs.collections.api.block.function.primitive.ByteFunction;
import com.gs.collections.api.block.function.primitive.CharFunction;
import com.gs.collections.api.block.function.primitive.DoubleFunction;
import com.gs.collections.api.block.function.primitive.DoubleObjectToDoubleFunction;
import com.gs.collections.api.block.function.primitive.FloatFunction;
import com.gs.collections.api.block.function.primitive.FloatObjectToFloatFunction;
import com.gs.collections.api.block.function.primitive.IntFunction;
import com.gs.collections.api.block.function.primitive.IntObjectToIntFunction;
import com.gs.collections.api.block.function.primitive.LongFunction;
import com.gs.collections.api.block.function.primitive.LongObjectToLongFunction;
import com.gs.collections.api.block.function.primitive.ShortFunction;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.block.predicate.Predicate2;
import com.gs.collections.api.block.procedure.Procedure;
import com.gs.collections.api.block.procedure.Procedure2;
import com.gs.collections.api.block.procedure.primitive.ObjectIntProcedure;
import com.gs.collections.api.collection.MutableCollection;
import com.gs.collections.api.list.ImmutableList;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.api.list.primitive.ImmutableBooleanList;
import com.gs.collections.api.list.primitive.ImmutableByteList;
import com.gs.collections.api.list.primitive.ImmutableCharList;
import com.gs.collections.api.list.primitive.ImmutableDoubleList;
import com.gs.collections.api.list.primitive.ImmutableFloatList;
import com.gs.collections.api.list.primitive.ImmutableIntList;
import com.gs.collections.api.list.primitive.ImmutableLongList;
import com.gs.collections.api.list.primitive.ImmutableShortList;
import com.gs.collections.api.map.ImmutableMap;
import com.gs.collections.api.multimap.MutableMultimap;
import com.gs.collections.api.multimap.list.ImmutableListMultimap;
import com.gs.collections.api.partition.list.PartitionImmutableList;
import com.gs.collections.api.stack.MutableStack;
import com.gs.collections.api.tuple.Pair;
import com.gs.collections.impl.block.factory.Comparators;
import com.gs.collections.impl.block.factory.Functions;
import com.gs.collections.impl.block.procedure.CollectIfProcedure;
import com.gs.collections.impl.block.procedure.CollectProcedure;
import com.gs.collections.impl.block.procedure.FlatCollectProcedure;
import com.gs.collections.impl.block.procedure.RejectProcedure;
import com.gs.collections.impl.block.procedure.SelectInstancesOfProcedure;
import com.gs.collections.impl.block.procedure.SelectProcedure;
import com.gs.collections.impl.block.procedure.primitive.CollectBooleanProcedure;
import com.gs.collections.impl.block.procedure.primitive.CollectByteProcedure;
import com.gs.collections.impl.block.procedure.primitive.CollectCharProcedure;
import com.gs.collections.impl.block.procedure.primitive.CollectDoubleProcedure;
import com.gs.collections.impl.block.procedure.primitive.CollectFloatProcedure;
import com.gs.collections.impl.block.procedure.primitive.CollectIntProcedure;
import com.gs.collections.impl.block.procedure.primitive.CollectLongProcedure;
import com.gs.collections.impl.block.procedure.primitive.CollectShortProcedure;
import com.gs.collections.impl.collection.immutable.AbstractImmutableCollection;
import com.gs.collections.impl.factory.Lists;
import com.gs.collections.impl.lazy.ReverseIterable;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.list.mutable.primitive.BooleanArrayList;
import com.gs.collections.impl.list.mutable.primitive.ByteArrayList;
import com.gs.collections.impl.list.mutable.primitive.CharArrayList;
import com.gs.collections.impl.list.mutable.primitive.DoubleArrayList;
import com.gs.collections.impl.list.mutable.primitive.FloatArrayList;
import com.gs.collections.impl.list.mutable.primitive.IntArrayList;
import com.gs.collections.impl.list.mutable.primitive.LongArrayList;
import com.gs.collections.impl.list.mutable.primitive.ShortArrayList;
import com.gs.collections.impl.multimap.list.FastListMultimap;
import com.gs.collections.impl.stack.mutable.ArrayStack;
import com.gs.collections.impl.utility.Iterate;
import com.gs.collections.impl.utility.ListIterate;
import net.jcip.annotations.Immutable;

/**
 * This class is the parent class for all ImmutableLists.  All implementations of ImmutableList must implement the List
 * interface so anArrayList.equals(anImmutableList) can return true when the contents and order are the same.
 */
@Immutable
abstract class AbstractImmutableList<T> extends AbstractImmutableCollection<T>
        implements ImmutableList<T>, List<T>
{
    public List<T> castToList()
    {
        return this;
    }

    @Override
    public boolean equals(Object otherList)
    {
        if (otherList == this)
        {
            return true;
        }
        if (!(otherList instanceof List))
        {
            return false;
        }
        List<?> list = (List<?>) otherList;
        if (list instanceof RandomAccess)
        {
            return this.randomAccessListEquals(list);
        }
        return this.regularListEquals(list);
    }

    protected boolean randomAccessListEquals(List<?> otherList)
    {
        if (this.size() != otherList.size())
        {
            return false;
        }
        int localSize = this.size();
        for (int i = 0; i < localSize; i++)
        {
            T one = this.get(i);
            Object two = otherList.get(i);
            if (!Comparators.nullSafeEquals(one, two))
            {
                return false;
            }
        }
        return true;
    }

    protected boolean regularListEquals(List<?> otherList)
    {
        Iterator<?> iterator = otherList.iterator();
        for (int i = 0; i < this.size(); i++)
        {
            T one = this.get(i);
            if (!iterator.hasNext())
            {
                return false;
            }
            Object two = iterator.next();
            if (!Comparators.nullSafeEquals(one, two))
            {
                return false;
            }
        }
        return !iterator.hasNext();
    }

    @Override
    public int hashCode()
    {
        int hashCode = 1;
        int localSize = this.size();
        for (int i = 0; i < localSize; i++)
        {
            T item = this.get(i);
            hashCode = 31 * hashCode + (item == null ? 0 : item.hashCode());
        }
        return hashCode;
    }

    public ImmutableList<T> newWithout(T element)
    {
        int indexToRemove = this.indexOf(element);
        if (indexToRemove < 0)
        {
            return this;
        }
        T[] results = (T[]) new Object[this.size() - 1];
        int currentIndex = 0;
        for (int i = 0; i < this.size(); i++)
        {
            T item = this.get(i);
            if (i != indexToRemove)
            {
                results[currentIndex++] = item;
            }
        }
        return Lists.immutable.of(results);
    }

    public ImmutableList<T> newWithAll(Iterable<? extends T> elements)
    {
        final int oldSize = this.size();
        int newSize = Iterate.sizeOf(elements);
        final T[] array = (T[]) new Object[oldSize + newSize];
        this.toArray(array);
        Iterate.forEachWithIndex(elements, new ObjectIntProcedure<T>()
        {
            public void value(T each, int index)
            {
                array[oldSize + index] = each;
            }
        });
        return Lists.immutable.of(array);
    }

    public ImmutableList<T> newWithoutAll(Iterable<? extends T> elements)
    {
        FastList<T> result = FastList.newListWith((T[]) this.toArray());
        this.removeAllFrom(elements, result);
        return result.toImmutable();
    }

    public T getFirst()
    {
        return this.isEmpty() ? null : this.get(0);
    }

    public T getLast()
    {
        return this.isEmpty() ? null : this.get(this.size() - 1);
    }

    public ImmutableList<T> select(Predicate<? super T> predicate)
    {
        MutableList<T> result = Lists.mutable.of();
        this.forEach(new SelectProcedure<T>(predicate, result));
        return result.toImmutable();
    }

    public <P> ImmutableList<T> selectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ListIterate.selectWith(this, predicate, parameter, FastList.<T>newList()).toImmutable();
    }

    @Override
    public <P, R extends Collection<T>> R selectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        return ListIterate.selectWith(this, predicate, parameter, targetCollection);
    }

    public ImmutableList<T> reject(Predicate<? super T> predicate)
    {
        MutableList<T> result = Lists.mutable.of();
        this.forEach(new RejectProcedure<T>(predicate, result));
        return result.toImmutable();
    }

    public <P> ImmutableList<T> rejectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ListIterate.rejectWith(this, predicate, parameter, FastList.<T>newList()).toImmutable();
    }

    @Override
    public <P, R extends Collection<T>> R rejectWith(
            Predicate2<? super T, ? super P> predicate,
            P parameter,
            R targetCollection)
    {
        return ListIterate.rejectWith(this, predicate, parameter, targetCollection);
    }

    public PartitionImmutableList<T> partition(Predicate<? super T> predicate)
    {
        return ListIterate.partition(this, predicate).toImmutable();
    }

    public <P> PartitionImmutableList<T> partitionWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ListIterate.partitionWith(this, predicate, parameter).toImmutable();
    }

    public <S> ImmutableList<S> selectInstancesOf(Class<S> clazz)
    {
        FastList<S> result = FastList.newList(this.size());
        this.forEach(new SelectInstancesOfProcedure<S>(clazz, result));
        return result.toImmutable();
    }

    public <V> ImmutableList<V> collect(Function<? super T, ? extends V> function)
    {
        MutableList<V> result = Lists.mutable.of();
        this.forEach(new CollectProcedure<T, V>(function, result));
        return result.toImmutable();
    }

    public ImmutableBooleanList collectBoolean(BooleanFunction<? super T> booleanFunction)
    {
        BooleanArrayList result = new BooleanArrayList();
        this.forEach(new CollectBooleanProcedure<T>(booleanFunction, result));
        return result.toImmutable();
    }

    public ImmutableByteList collectByte(ByteFunction<? super T> byteFunction)
    {
        ByteArrayList result = new ByteArrayList();
        this.forEach(new CollectByteProcedure<T>(byteFunction, result));
        return result.toImmutable();
    }

    public ImmutableCharList collectChar(CharFunction<? super T> charFunction)
    {
        CharArrayList result = new CharArrayList();
        this.forEach(new CollectCharProcedure<T>(charFunction, result));
        return result.toImmutable();
    }

    public ImmutableDoubleList collectDouble(DoubleFunction<? super T> doubleFunction)
    {
        DoubleArrayList result = new DoubleArrayList();
        this.forEach(new CollectDoubleProcedure<T>(doubleFunction, result));
        return result.toImmutable();
    }

    public ImmutableFloatList collectFloat(FloatFunction<? super T> floatFunction)
    {
        FloatArrayList result = new FloatArrayList();
        this.forEach(new CollectFloatProcedure<T>(floatFunction, result));
        return result.toImmutable();
    }

    public ImmutableIntList collectInt(IntFunction<? super T> intFunction)
    {
        IntArrayList result = new IntArrayList();
        this.forEach(new CollectIntProcedure<T>(intFunction, result));
        return result.toImmutable();
    }

    public ImmutableLongList collectLong(LongFunction<? super T> longFunction)
    {
        LongArrayList result = new LongArrayList(this.size());
        this.forEach(new CollectLongProcedure<T>(longFunction, result));
        return result.toImmutable();
    }

    public ImmutableShortList collectShort(ShortFunction<? super T> shortFunction)
    {
        ShortArrayList result = new ShortArrayList();
        this.forEach(new CollectShortProcedure<T>(shortFunction, result));
        return result.toImmutable();
    }

    public <P, V> ImmutableList<V> collectWith(Function2<? super T, ? super P, ? extends V> function, P parameter)
    {
        return this.collect(Functions.bind(function, parameter));
    }

    public <V> ImmutableList<V> collectIf(
            Predicate<? super T> predicate,
            Function<? super T, ? extends V> function)
    {
        MutableList<V> result = Lists.mutable.of();
        this.forEach(new CollectIfProcedure<T, V>(result, function, predicate));
        return result.toImmutable();
    }

    @Override
    public <P, V, R extends Collection<V>> R collectWith(
            Function2<? super T, ? super P, ? extends V> function, P parameter, R targetCollection)
    {
        return ListIterate.collectWith(this, function, parameter, targetCollection);
    }

    public <V> ImmutableList<V> flatCollect(Function<? super T, ? extends Iterable<V>> function)
    {
        MutableList<V> result = Lists.mutable.of();
        this.forEach(new FlatCollectProcedure<T, V>(function, result));
        return result.toImmutable();
    }

    @Override
    public <V, R extends Collection<V>> R flatCollect(
            Function<? super T, ? extends Iterable<V>> function, R target)
    {
        return ListIterate.flatCollect(this, function, target);
    }

    @Override
    public T detect(Predicate<? super T> predicate)
    {
        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            T item = this.get(i);
            if (predicate.accept(item))
            {
                return item;
            }
        }
        return null;
    }

    @Override
    public <P> T detectWith(Predicate2<? super T, ? super P> predicate, P parameter)
    {
        return ListIterate.detectWith(this, predicate, parameter);
    }

    @Override
    public <P> T detectWithIfNone(Predicate2<? super T, ? super P> predicate, P parameter, Function0<? extends T> function)
    {
        T result = this.detectWith(predicate, parameter);
        return result == null ? function.value() : result;
    }

    @Override
    public int count(Predicate<? super T> predicate)
    {
        int count = 0;
        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            if (predicate.accept(this.get(i)))
            {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean anySatisfy(Predicate<? super T> predicate)
    {
        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            if (predicate.accept(this.get(i)))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean allSatisfy(Predicate<? super T> predicate)
    {
        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            if (!predicate.accept(this.get(i)))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean noneSatisfy(Predicate<? super T> predicate)
    {
        int size = this.size();
        for (int i = 0; i < size; i++)
        {
            if (predicate.accept(this.get(i)))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public <IV> IV injectInto(IV injectedValue, Function2<? super IV, ? super T, ? extends IV> function)
    {
        return ListIterate.injectInto(injectedValue, this, function);
    }

    @Override
    public int injectInto(int injectedValue, IntObjectToIntFunction<? super T> function)
    {
        return ListIterate.injectInto(injectedValue, this, function);
    }

    @Override
    public long injectInto(long injectedValue, LongObjectToLongFunction<? super T> function)
    {
        return ListIterate.injectInto(injectedValue, this, function);
    }

    @Override
    public double injectInto(double injectedValue, DoubleObjectToDoubleFunction<? super T> function)
    {
        return ListIterate.injectInto(injectedValue, this, function);
    }

    @Override
    public float injectInto(float injectedValue, FloatObjectToFloatFunction<? super T> function)
    {
        return ListIterate.injectInto(injectedValue, this, function);
    }

    @Override
    public long sumOfInt(IntFunction<? super T> function)
    {
        return ListIterate.sumOfInt(this, function);
    }

    @Override
    public long sumOfLong(LongFunction<? super T> function)
    {
        return ListIterate.sumOfLong(this, function);
    }

    @Override
    public double sumOfFloat(FloatFunction<? super T> function)
    {
        return ListIterate.sumOfFloat(this, function);
    }

    @Override
    public double sumOfDouble(DoubleFunction<? super T> function)
    {
        return ListIterate.sumOfDouble(this, function);
    }

    @Override
    public void forEachWithIndex(ObjectIntProcedure<? super T> objectIntProcedure)
    {
        int localSize = this.size();
        for (int i = 0; i < localSize; i++)
        {
            T each = this.get(i);
            objectIntProcedure.value(each, i);
        }
    }

    @Override
    public <P> void forEachWith(Procedure2<? super T, ? super P> procedure, P parameter)
    {
        int localSize = this.size();
        for (int i = 0; i < localSize; i++)
        {
            T each = this.get(i);
            procedure.value(each, parameter);
        }
    }

    public void forEach(
            int from, int to, Procedure<? super T> procedure)
    {
        if (from < 0 || to < 0)
        {
            throw new IllegalArgumentException("Neither from nor to may be negative.");
        }
        if (from <= to)
        {
            for (int i = from; i <= to; i++)
            {
                procedure.value(this.get(i));
            }
        }
        else
        {
            for (int i = from; i >= to; i--)
            {
                procedure.value(this.get(i));
            }
        }
    }

    public void forEachWithIndex(
            int from, int to, ObjectIntProcedure<? super T> objectIntProcedure)
    {
        if (from < 0 || to < 0)
        {
            throw new IllegalArgumentException("Neither from nor to may be negative.");
        }
        if (from <= to)
        {
            for (int i = from; i <= to; i++)
            {
                objectIntProcedure.value(this.get(i), i);
            }
        }
        else
        {
            for (int i = from; i >= to; i--)
            {
                objectIntProcedure.value(this.get(i), i);
            }
        }
    }

    public void reverseForEach(Procedure<? super T> procedure)
    {
        if (this.notEmpty())
        {
            this.forEach(this.size() - 1, 0, procedure);
        }
    }

    public int indexOf(Object object)
    {
        int n = this.size();
        if (object == null)
        {
            for (int i = 0; i < n; i++)
            {
                if (this.get(i) == null)
                {
                    return i;
                }
            }
        }
        else
        {
            for (int i = 0; i < n; i++)
            {
                if (object.equals(this.get(i)))
                {
                    return i;
                }
            }
        }
        return -1;
    }

    public int lastIndexOf(Object object)
    {
        int n = this.size() - 1;
        if (object == null)
        {
            for (int i = n; i >= 0; i--)
            {
                if (this.get(i) == null)
                {
                    return i;
                }
            }
        }
        else
        {
            for (int i = n; i >= 0; i--)
            {
                if (object.equals(this.get(i)))
                {
                    return i;
                }
            }
        }
        return -1;
    }

    public Iterator<T> iterator()
    {
        return this.listIterator(0);
    }

    public boolean addAll(int index, Collection<? extends T> collection)
    {
        throw new UnsupportedOperationException("Cannot call addAll() on " + this.getClass().getSimpleName());
    }

    public T set(int index, T element)
    {
        throw new UnsupportedOperationException("Cannot call set() on " + this.getClass().getSimpleName());
    }

    public void add(int index, T element)
    {
        throw new UnsupportedOperationException("Cannot call add() on " + this.getClass().getSimpleName());
    }

    public T remove(int index)
    {
        throw new UnsupportedOperationException("Cannot call remove() on " + this.getClass().getSimpleName());
    }

    public ListIterator<T> listIterator()
    {
        return new ImmutableListIterator<T>(this, 0);
    }

    public ListIterator<T> listIterator(int index)
    {
        if (index < 0 || index > this.size())
        {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        return new ImmutableListIterator<T>(this, index);
    }

    public List<T> subList(int fromIndex, int toIndex)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".subList() not implemented yet");
    }

    public ImmutableList<T> distinct()
    {
        MutableList<T> result = Lists.mutable.of();
        ListIterate.distinct(this.castToList(), result);
        return result.toImmutable();
    }

    @Override
    public void appendString(Appendable appendable, String start, String separator, String end)
    {
        ListIterate.appendString(this, appendable, start, separator, end);
    }

    public <V> ImmutableListMultimap<V, T> groupBy(Function<? super T, ? extends V> function)
    {
        return this.groupBy(function, FastListMultimap.<V, T>newMultimap()).toImmutable();
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupBy(
            Function<? super T, ? extends V> function,
            R target)
    {
        return ListIterate.groupBy(this, function, target);
    }

    public <V> ImmutableListMultimap<V, T> groupByEach(Function<? super T, ? extends Iterable<V>> function)
    {
        return this.groupByEach(function, FastListMultimap.<V, T>newMultimap()).toImmutable();
    }

    @Override
    public <V, R extends MutableMultimap<V, T>> R groupByEach(
            Function<? super T, ? extends Iterable<V>> function,
            R target)
    {
        return ListIterate.groupByEach(this, function, target);
    }

    public <V> ImmutableMap<V, T> groupByUniqueKey(Function<? super T, ? extends V> function)
    {
        throw new UnsupportedOperationException(this.getClass().getSimpleName() + ".groupByUniqueKey() not implemented yet");
    }

    @Override
    public T min(Comparator<? super T> comparator)
    {
        return ListIterate.min(this, comparator);
    }

    @Override
    public T max(Comparator<? super T> comparator)
    {
        return ListIterate.max(this, comparator);
    }

    @Override
    public T min()
    {
        return ListIterate.min(this);
    }

    @Override
    public T max()
    {
        return ListIterate.max(this);
    }

    @Override
    public <V extends Comparable<? super V>> T minBy(Function<? super T, ? extends V> function)
    {
        return ListIterate.minBy(this, function);
    }

    @Override
    public <V extends Comparable<? super V>> T maxBy(Function<? super T, ? extends V> function)
    {
        return ListIterate.maxBy(this, function);
    }

    public <S> ImmutableList<Pair<T, S>> zip(Iterable<S> that)
    {
        return this.zip(that, FastList.<Pair<T, S>>newList()).toImmutable();
    }

    public ImmutableList<Pair<T, Integer>> zipWithIndex()
    {
        return this.zipWithIndex(FastList.<Pair<T, Integer>>newList()).toImmutable();
    }

    public ImmutableList<T> takeWhile(Predicate<? super T> predicate)
    {
        return ListIterate.takeWhile(this, predicate).toImmutable();
    }

    public ImmutableList<T> dropWhile(Predicate<? super T> predicate)
    {
        return ListIterate.dropWhile(this, predicate).toImmutable();
    }

    public PartitionImmutableList<T> partitionWhile(Predicate<? super T> predicate)
    {
        return ListIterate.partitionWhile(this, predicate).toImmutable();
    }

    @Override
    protected MutableCollection<T> newMutable(int size)
    {
        return FastList.newList(size);
    }

    public MutableStack<T> toStack()
    {
        return ArrayStack.newStack(this);
    }

    public ReverseIterable<T> asReversed()
    {
        return ReverseIterable.adapt(this);
    }

    public int binarySearch(T key, Comparator<? super T> comparator)
    {
        return Collections.binarySearch(this, key, comparator);
    }

    public int binarySearch(T key)
    {
        return Collections.binarySearch((List<? extends Comparable<? super T>>) this, key);
    }

    public ImmutableList<T> toImmutable()
    {
        return this;
    }
}
