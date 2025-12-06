// AICareerBuddy-BussinesLayer/Services/RegistrationService.cs
using AICareerBuddy_Entities.Entities;
using AICareerBuddy_DataAccessLayer.Repositories;
using System;
using Microsoft.AspNetCore.Identity;
using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;

namespace AICareerBuddy_BussinesLogic.Services
{
    // DTO for incoming registration data
    public class RegistrationRequestDto
    {
        [JsonPropertyName("first_name")]
        public string FirstName { get; set; }
        [JsonPropertyName("last_name")]
        public string LastName { get; set; }
        [JsonPropertyName("username")]
        public string Username { get; set; }
        [JsonPropertyName("email")]
        public string Email { get; set; }
        [JsonPropertyName("password")]
        public string Password { get; set; }
        [JsonPropertyName("role")]
        public string Role { get; set; }
    }

    // DTO for sending response data
    public class RegistrationResponseDto
    {
        [JsonPropertyName("userId")]
        public int UserId { get; set; }
        [JsonPropertyName("username")]
        public string Username { get; set; }
    }

    public class RegistrationService
    {
        private readonly PasswordHasher<User> _passwordHasher = new PasswordHasher<User>();

        public RegistrationResponseDto RegisterUser(RegistrationRequestDto dto)
        {
            // Basic validation
            if (string.IsNullOrWhiteSpace(dto.FirstName) || string.IsNullOrWhiteSpace(dto.LastName) ||
                string.IsNullOrWhiteSpace(dto.Username) || string.IsNullOrWhiteSpace(dto.Email) ||
                string.IsNullOrWhiteSpace(dto.Password))
            {
                throw new ArgumentException("All fields are required.");
            }
            if (dto.Password.Length < 6)
            {
                throw new ArgumentException("Password must be at least 6 characters long.");
            }
            // Check for duplicate email
            if (UserRepo.GetUserByEmail(dto.Email) != null)
            {
                throw new ArgumentException("Email is already registered.");
            }
            // (Optional) Check for duplicate username
            if (UserRepo.GetUserByUsername(dto.Username) != null)
            {
                throw new ArgumentException("Username is already taken.");
            }
            // Create user entity
            var user = new User
            {
                FirstName = dto.FirstName.Trim(),
                LastName = dto.LastName.Trim(),
                Username = dto.Username.Trim(),
                Email = dto.Email.Trim(),
                Role = dto.Role.Trim(),
                CreatedAt = DateTime.UtcNow
            };
            // Hash password
            user.PasswordHash = _passwordHasher.HashPassword(user, dto.Password);
            // Save to "database"
            UserRepo.CreateUser(user);
            // Prepare response
            return new RegistrationResponseDto
            {
                UserId = user.Id,
                Username = user.Username
            };
        }
    }
}
