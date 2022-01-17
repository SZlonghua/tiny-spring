package us.codecraft.tinyioc.aop;

public interface AspectJPrecedenceInformation {

    String getAspectName();

    int getDeclarationOrder();

    boolean isBeforeAdvice();

    boolean isAfterAdvice();
}
