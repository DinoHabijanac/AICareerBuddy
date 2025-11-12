namespace AI_CareerBuddy_Backend.DTOs
{
    public class LoginResponse
    {
        public bool Success { get; set; }
        public string Message { get; set; } = string.Empty;
        public string? Token { get; set; }
        public object? User { get; set; } // anon. shape za vraćanje osnovnih podataka
    }
}
