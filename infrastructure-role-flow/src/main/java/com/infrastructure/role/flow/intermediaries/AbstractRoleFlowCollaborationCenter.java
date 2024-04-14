package com.infrastructure.role.flow.intermediaries;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ResolvableType;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
@Slf4j
public abstract class AbstractRoleFlowCollaborationCenter implements RoleFlowCollaborationCenter {

    private final Mediators mediators = new Mediators();


    protected Intermediaries matchMediator(Class request, Class response){
        Intermediaries intermediaries = mediators.getMediator(MediatorPair.newInstance(request, response));
        if(intermediaries == null){
            log.warn("没有找到对应的中介者 source：{} target：{}",request.getSimpleName(),response.getSimpleName());
        }
        return intermediaries;
    }

    @Override
    public void addMediator(Intermediaries intermediaries) {
        mediators.add(intermediaries);
    }

    @Override
    public void clear() {
        mediators.clear();
    }

    private static class Mediators{
        
        Map<MediatorPair, Intermediaries> mediatorMap = new HashMap<>();
        
        public void add(Intermediaries intermediaries) {
            ResolvableType resolvableType = ResolvableType.forClass(intermediaries.getClass()).as(Intermediaries.class);
            ResolvableType[] generics = resolvableType.getGenerics();
            MediatorPair mediatorPair = new MediatorPair(generics[0].resolve(),generics[1].resolve());
            mediatorMap.putIfAbsent(mediatorPair, intermediaries);
        }

        public Intermediaries getMediator(MediatorPair mediatorPair){
            return mediatorMap.get(mediatorPair);
        }

        public void clear(){
            mediatorMap.clear();
        }

    }
    
    private final static class MediatorPair{

        private final Class<?> sourceType;

        private final Class<?> targetType;
        
        public MediatorPair(Class<?> sourceType,Class<?> targetType){
            this.sourceType = sourceType;
            this.targetType = targetType;
        }


        /**
         * Returns a hash code value for the object. This method is
         * supported for the benefit of hash tables such as those provided by
         * {@link HashMap}.
         * <p>
         * The general contract of {@code hashCode} is:
         * <ul>
         * <li>Whenever it is invoked on the same object more than once during
         *     an execution of a Java application, the {@code hashCode} method
         *     must consistently return the same integer, provided no information
         *     used in {@code equals} comparisons on the object is modified.
         *     This integer need not remain consistent from one execution of an
         *     application to another execution of the same application.
         * <li>If two objects are equal according to the {@code equals(Object)}
         *     method, then calling the {@code hashCode} method on each of
         *     the two objects must produce the same integer result.
         * <li>It is <em>not</em> required that if two objects are unequal
         *     according to the {@link Object#equals(Object)}
         *     method, then calling the {@code hashCode} method on each of the
         *     two objects must produce distinct integer results.  However, the
         *     programmer should be aware that producing distinct integer results
         *     for unequal objects may improve the performance of hash tables.
         * </ul>
         * <p>
         * As much as is reasonably practical, the hashCode method defined by
         * class {@code Object} does return distinct integers for distinct
         * objects. (This is typically implemented by converting the internal
         * address of the object into an integer, but this implementation
         * technique is not required by the
         * Java&trade; programming language.)
         *
         * @return a hash code value for this object.
         * @see Object#equals(Object)
         * @see System#identityHashCode
         */
        @Override
        public int hashCode() {
            return (this.sourceType.hashCode() * 31 + this.targetType.hashCode());
        }

        /**
         * Indicates whether some other object is "equal to" this one.
         * <p>
         * The {@code equals} method implements an equivalence relation
         * on non-null object references:
         * <ul>
         * <li>It is <i>reflexive</i>: for any non-null reference value
         *     {@code x}, {@code x.equals(x)} should return
         *     {@code true}.
         * <li>It is <i>symmetric</i>: for any non-null reference values
         *     {@code x} and {@code y}, {@code x.equals(y)}
         *     should return {@code true} if and only if
         *     {@code y.equals(x)} returns {@code true}.
         * <li>It is <i>transitive</i>: for any non-null reference values
         *     {@code x}, {@code y}, and {@code z}, if
         *     {@code x.equals(y)} returns {@code true} and
         *     {@code y.equals(z)} returns {@code true}, then
         *     {@code x.equals(z)} should return {@code true}.
         * <li>It is <i>consistent</i>: for any non-null reference values
         *     {@code x} and {@code y}, multiple invocations of
         *     {@code x.equals(y)} consistently return {@code true}
         *     or consistently return {@code false}, provided no
         *     information used in {@code equals} comparisons on the
         *     objects is modified.
         * <li>For any non-null reference value {@code x},
         *     {@code x.equals(null)} should return {@code false}.
         * </ul>
         * <p>
         * The {@code equals} method for class {@code Object} implements
         * the most discriminating possible equivalence relation on objects;
         * that is, for any non-null reference values {@code x} and
         * {@code y}, this method returns {@code true} if and only
         * if {@code x} and {@code y} refer to the same object
         * ({@code x == y} has the value {@code true}).
         * <p>
         * Note that it is generally necessary to override the {@code hashCode}
         * method whenever this method is overridden, so as to maintain the
         * general contract for the {@code hashCode} method, which states
         * that equal objects must have equal hash codes.
         *
         * @param obj the reference object with which to compare.
         * @return {@code true} if this object is the same as the obj
         * argument; {@code false} otherwise.
         * @see #hashCode()
         * @see HashMap
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || obj.getClass() != MediatorPair.class) {
                return false;
            }
            MediatorPair mediatorPair = (MediatorPair) obj;
            return (this.sourceType == mediatorPair.sourceType && this.targetType == mediatorPair.targetType);
        }

        public static MediatorPair newInstance(Class<?> sourceType,Class<?> targetType) {


            return new MediatorPair(sourceType,targetType);
        }
    }
}
