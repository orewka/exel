import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Map<String, String> exel1 = new HashMap<>(Map.
                of("10K_1%_0603", "R1,R2,R3,R4",
                        "20pF_50V_0603", "C1,C2,C3,C4",
                        "30pF_50V_0603","C200,C201",
                        "1000pf_50v_0402", "C999"));
        Map<String, String> exel2 = new HashMap<>(Map.
                of("10K_1%_0603", "R3,R4,R5,R6",
                        "20pF_50V_0603", "C3,C4,C5,C6",
                        "100R_1%_0603", "R200,R201",
                        "1000pf_50v_0402", "C999,C1000"));
        Map<String, List<List<String>>> otli4ia = new HashMap<>();
        exel1 = exel1.entrySet().stream()
                .filter(n -> {
                    if (exel2.get(n.getKey()) != null) {
                        otli4ia.put(n.getKey(), calculate(n.getValue(), exel2.get(n.getKey())));
                        exel2.remove(n.getKey());
                        return false;
                    }
                    return true;
                }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        System.out.println("В программе отсутствуют полностью : ");
        exel1.forEach((key, value) -> System.out.println(key + " - " + value));
        System.out.println("В спецификации отсутствуют полностью : ");
        exel2.forEach((key, value) -> System.out.println(key + " - " + value));
        System.out.println("Имеются различия : ");
        otli4ia.forEach((key, value) -> {
            System.out.println(key + " :");
            System.out.println("Нет в программе - " + value.get(0));
            System.out.println("Нет в спецификации - " + value.get(1));
        });
    }

    public static List<List<String>> calculate(String string1, String string2) {
        List<String> list1 = new ArrayList<>(check(string1));
        List<String> list2 = new ArrayList<>(check(string2));
        list1 = list1.stream().filter(x -> {
            if (list2.contains(x)) {
                list2.remove(x);
                return false;
            }
            return true;
        }).collect(Collectors.toList());
        List<List<String>> res = new ArrayList<>();
        res.add(list1);
        res.add(list2);
        return res;
    }

    public static List<String> check(String string) {
        if (!string.contains(",")) {
            List<String> list = new ArrayList<>();
            list.add(string);
            return list;
        }
        if (string.contains("-")) {
            return pasreList(Arrays.stream(string.split(",")).toList());
        }
        return Arrays.stream(string.split(",")).toList();
    }

    public static List<String> pasreList(List<String> parseList) {
        List<String> parseListRes = new ArrayList<>();
        AtomicInteger x1 = new AtomicInteger();
        AtomicInteger y1 = new AtomicInteger();
        AtomicReference<String> temp = new AtomicReference<>("");
        parseList.forEach(n -> {
            if (n.contains("-")) {
                Arrays.stream(n.split("-"))
                        .map(n2 -> {
                            temp.set(n2.replaceAll("[0-9]", ""));
                            return n2.replaceAll("[^0-9]", "");
                        })
                        .map(Integer::parseInt)
                        .reduce((x, y) -> {
                            x1.set(x);
                            y1.set(y);
                            return y - x;
                        })
                        .ifPresent(n2 -> {
                            for (int i = x1.get(); i < y1.get() + 1; i++) {
                                parseListRes.add(temp + String.valueOf(i));
                            }
                        });
            } else {
                parseListRes.add(n);
            }
        });
        return parseListRes;
    }
}