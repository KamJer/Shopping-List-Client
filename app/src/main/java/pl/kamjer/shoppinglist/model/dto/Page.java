package pl.kamjer.shoppinglist.model.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Page<T> {
    private List<T> content = new ArrayList<>();
    private int number;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;

    public <R> Page<R> map(Function<T, R> mapper) {
        List<R> mappedContent = content.stream()
                .map(mapper)
                .collect(Collectors.toList());

        Page<R> newPage = new Page<>();
        newPage.content = mappedContent;
        newPage.number = this.number;
        newPage.size = this.size;
        newPage.totalElements = this.totalElements;
        newPage.totalPages = this.totalPages;
        newPage.first = this.first;
        newPage.last = this.last;
        newPage.hasNext = this.hasNext;
        newPage.hasPrevious = this.hasPrevious;

        return newPage;
    }

    public void remove(T element) {
        content.remove(element);
        totalElements--;
    }

    public void add(T element) {
        content.add(element);
        totalElements++;
    }
}
