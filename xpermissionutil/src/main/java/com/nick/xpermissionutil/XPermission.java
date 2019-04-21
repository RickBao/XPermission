package com.nick.xpermissionutil;


import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

public class XPermission {

    public final String name;
    public final boolean granted;
    public final boolean shouldShowRequestPermissionRationalbe;

    public XPermission(String name, boolean granted) {
        this(name, granted, false);
    }

    public XPermission(String name, boolean granted, boolean shouldShowRequestPermissionRationalbe) {
        this.name = name;
        this.granted = granted;
        this.shouldShowRequestPermissionRationalbe = shouldShowRequestPermissionRationalbe;
    }

    public XPermission(List<XPermission> permissions) {
        name = combineName(permissions);
        granted = combineGranted(permissions);
        shouldShowRequestPermissionRationalbe = combineShouldShowRequestPermissionRationable(permissions);
    }


    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (granted ? 1 : 0);
        result = 31 * result + (shouldShowRequestPermissionRationalbe ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        final XPermission that = (XPermission) obj;
        if (granted != that.granted) return false;
        if (shouldShowRequestPermissionRationalbe != that.shouldShowRequestPermissionRationalbe)
            return false;
        return name.equals(that.name);
    }

    @Override
    public String toString() {
        return "XPermission{" +
                "name='" + name + '\'' +
                ", granted=" + granted +
                ", shouldShowRequestPermissionRationale=" + shouldShowRequestPermissionRationalbe +
                '}';
    }

    private String combineName(List<XPermission> permissions) {
        return Observable.fromIterable(permissions)
                .map(new Function<XPermission, String>() {
                    @Override
                    public String apply(XPermission xPermission) throws Exception {
                        return xPermission.name;
                    }
                }).collectInto(new StringBuilder(), new BiConsumer<StringBuilder, String>() {
                    @Override
                    public void accept(StringBuilder stringBuilder, String s) throws Exception {
                        if (stringBuilder.length() == 0) {
                            stringBuilder.append(s);
                        } else {
                            stringBuilder.append(", ").append(s);
                        }
                    }
                }).blockingGet().toString();
    }

    /**
     * RxJava # all,如果有一个为false，则返回false；
     * Predicate # test 给定一个参数T，返回boolean类型结果
     * @param permissions 用户请求的危险权限列表
     * @return 是否还有为被授权的权限
     */
    private Boolean combineGranted(List<XPermission> permissions) {
        return Observable.fromIterable(permissions)
                .all(new Predicate<XPermission>() {
                    @Override
                    public boolean test(XPermission xPermission) throws Exception {
                        return xPermission.granted;
                    }
                }).blockingGet();
    }

    /**
     * RxJava # all,如果有一个为false，则返回false；
     * @param permissions
     * @return
     */
    private Boolean combineShouldShowRequestPermissionRationable(final List<XPermission> permissions) {
        return Observable.fromIterable(permissions)
                .all(new Predicate<XPermission>() {
                    @Override
                    public boolean test(XPermission xPermission) throws Exception {
                        return xPermission.shouldShowRequestPermissionRationalbe;
                    }
                }).blockingGet();
    }
}
