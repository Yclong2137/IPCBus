package com.ycl.ipc.bus;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author Yclong
 */
class ServiceCache {


    private static final Set<TransformBinder> sBinderRegistry = new CopyOnWriteArraySet<>();


    static void addService(TransformBinder service) {
        sBinderRegistry.add(service);
    }


    static TransformBinder getService(String name) {
        return getBinder(new Predicate() {
            @Override
            public boolean test(TransformBinder transformBinder) {
                return name != null && name.equals(transformBinder.getInterfaceName());
            }
        });
    }

    static TransformBinder getServiceByServer(Object server) {
        return getBinder(new Predicate() {
            @Override
            public boolean test(TransformBinder transformBinder) {
                return server == transformBinder.getServer();
            }
        });
    }

    static void removeBinderByServer(Object server) {
        TransformBinder binder = getServiceByServer(server);
        if (binder != null) {
            sBinderRegistry.remove(binder);
        }
    }


    private static TransformBinder getBinder(Predicate predicate) {
        for (TransformBinder binder : sBinderRegistry) {
            if (binder != null && predicate != null && predicate.test(binder)) {
                return binder;
            }
        }
        return null;
    }


    public interface Predicate {

        boolean test(TransformBinder transformBinder);

    }


}
