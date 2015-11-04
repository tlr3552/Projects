package controllers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javafx.event.Event;
import javafx.event.EventHandler;

/**
 * An event handler which takes an object and method name and when called
 * will call through to the named method in the object.
 * 
 * @author Drew Heintz
 *
 * @param <E> The subclass of Event we will be using
 */
public class DispatchHandler<E extends Event> implements EventHandler<E> {
    
    private Object handlerObject;
    private Method handlerMethod;
    
    public DispatchHandler(Object obj, String methodName) {
        handlerObject = obj;
        
        // Find the named method
        for(Method method : obj.getClass().getMethods()) {
            Class<?> params[] = method.getParameterTypes();
            // If the method name matches and the first parameter is a
            // subclass of Event then we found the correct method. This
            // is NOT foolproof but it will work pretty well.
            if(method.getName().equals(methodName)
                    && params.length == 1
                    && Event.class.isAssignableFrom(params[0])) {
                handlerMethod = method;
                break;
            }
        }
        
        // Perform safety checks
        if(handlerMethod == null) {
            throw new IllegalArgumentException("Method " + obj.getClass()
                    + "#" + methodName + " not found");
        }
    }
    
    @Override
    public void handle(E event) {
        // Attempt to invoke the handler method 
        try {
            handlerMethod.invoke(handlerObject, event);
        } catch (IllegalAccessException | IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.getCause().printStackTrace();
        }
    }
}
