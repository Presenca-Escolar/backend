package school_attendance_backend.school_attendance_backend.dto;

public record AuthResponseDTO (
        String token,
        String username,
        String role
) {
}
