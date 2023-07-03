package com.task.testtask;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Union {
  final int currentUnionIndex;
  int topUnionIndex;
  int bottomUnionIndex;
  int leftUnionIndex;
  int rightUnionIndex;
}
