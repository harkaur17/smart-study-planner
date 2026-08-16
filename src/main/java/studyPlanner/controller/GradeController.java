package studyPlanner.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import studyPlanner.model.GradeItem;
import studyPlanner.service.GradeItemService;
import java.util.List;

@RestController
@RequestMapping("/api/courses/{courseId}/grades")
public class GradeController {

    @Autowired
    private GradeItemService gradeItemService;

    static class GradeItemRequest {
        public String name;
        public Double weight;
        public Double grade;
        public Double expectedGrade;
    }

    @GetMapping
    public ResponseEntity<List<GradeItem>> getGradeItems(@PathVariable Long courseId) {
        List<GradeItem> items = gradeItemService.getGradeItems(courseId);
        if (items == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(items);
    }

    @PostMapping
    public ResponseEntity<GradeItem> addGradeItem(@PathVariable Long courseId,
            @RequestBody GradeItemRequest request) {
        GradeItem item = gradeItemService.addGradeItem(courseId, request.name,
                request.weight, request.grade, request.expectedGrade);
        if (item == null)
            return ResponseEntity.badRequest().build();
        return ResponseEntity.status(201).body(item);
    }

    @PutMapping("/{itemId}")
    public ResponseEntity<GradeItem> editGradeItem(@PathVariable Long courseId,
            @PathVariable Long itemId,
            @RequestBody GradeItemRequest request) {
        GradeItem item = gradeItemService.editGradeItem(itemId, request.name,
                request.weight, request.grade, request.expectedGrade);
        if (item == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteGradeItem(@PathVariable Long courseId,
            @PathVariable Long itemId) {
        boolean result = gradeItemService.deleteGradeItem(itemId);
        if (result)
            return ResponseEntity.ok().build();
        return ResponseEntity.notFound().build();
    }
}