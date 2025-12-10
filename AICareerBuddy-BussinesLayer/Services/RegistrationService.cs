// AICareerBuddy-BussinesLayer/Services/RegistrationService.cs
using AICareerBuddy_Entities.Entities;
using AICareerBuddy_DataAccessLayer.Repositories;
using System;
using System.ComponentModel.DataAnnotations;
using System.Text.Json.Serialization;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Identity;

namespace AICareerBuddy_BussinesLogic.Services
{
    // DTO za ulazne podatke registracije
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

    // DTO za izlazne podatke (odgovor)
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
        private readonly IUserRepository _userRepository;

        public RegistrationService(IUserRepository userRepository)
        {
            _userRepository = userRepository ?? throw new ArgumentNullException(nameof(userRepository));
        }

        public async Task<RegistrationResponseDto> RegisterUserAsync(RegistrationRequestDto dto)
        {
            if (dto == null)
                throw new ArgumentNullException(nameof(dto));

            // Osnovna validacija
            if (string.IsNullOrWhiteSpace(dto.FirstName) ||
                string.IsNullOrWhiteSpace(dto.LastName) ||
                string.IsNullOrWhiteSpace(dto.Username) ||
                string.IsNullOrWhiteSpace(dto.Email) ||
                string.IsNullOrWhiteSpace(dto.Password))
            {
                throw new ArgumentException("All fields are required.");
            }

            if (dto.Password.Length < 6)
            {
                throw new ArgumentException("Password must be at least 6 characters long.");
            }

            // Validacija email-a
            var emailAttribute = new EmailAddressAttribute();
            if (!emailAttribute.IsValid(dto.Email))
            {
                throw new ArgumentException("Invalid email address format.");
            }

            // Provjera jedinstvenosti email-a u bazi
            var emailExists = await _userRepository.EmailExistsAsync(dto.Email);
            if (emailExists)
            {
                throw new ArgumentException("A user with this email already exists.");
            }

            // Kreiranje entiteta User
            var user = new User
            {
                FirstName = dto.FirstName.Trim(),
                LastName = dto.LastName.Trim(),
                Username = dto.Username.Trim(),
                Email = dto.Email.Trim(),
                Role = string.IsNullOrWhiteSpace(dto.Role) ? "User" : dto.Role.Trim(),
                CreatedAt = DateTime.UtcNow
            };

            // Hashiranje lozinke
            user.Password = _passwordHasher.HashPassword(user, dto.Password);

            // Spremanje u bazu preko repozitorija
            user = await _userRepository.CreateUserAsync(user);

            // Priprema odgovora
            return new RegistrationResponseDto
            {
                UserId = user.Id,
                Username = user.Username
            };
        }
    }
}
