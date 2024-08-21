package pl.kamjer.shoppinglist.model.dto;

import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import pl.kamjer.shoppinglist.model.ModifyState;

@AllArgsConstructor
@Builder
@Getter
public class CategoryDto {

    private long categoryId;
    private String categoryName;
    private boolean deleted;
    private ModifyState modifyState;

}
