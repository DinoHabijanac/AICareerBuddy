// AICareerBuddy-BussinesLayer/Services/RegistrationService.cs
using AICareerBuddy_Entities.Entities;
using AICareerBuddy_DataAccessLayer.Repositories;
using System;
using Microsoft.AspNetCore.Identity;
using System.Text.Json.Serialization;

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

    // DTO za povratne podatke registracije
    public class RegistrationResponseDto
    {
        [JsonPropertyName("userId")]
        public int UserId { get; set; }
        [JsonPropertyName("username")]
        public string Username { get; set; }
    }

    // **NOVO**: DTO za ulazne podatke prijave
    public class LoginRequestDto
    {
        [JsonPropertyName("username")]
        public string Username { get; set; }
        [JsonPropertyName("password")]
        public string Password { get; set; }
    }

    // **NOVO**: DTO za povratne podatke prijave
    public class LoginResponseDto
    {
        [JsonPropertyName("userId")]
        public int UserId { get; set; }
        [JsonPropertyName("username")]
        public string Username { get; set; }
    }

    public class AuthService
    {
        // PasswordHasher za hashiranje i provjeru lozinke
        private readonly PasswordHasher<User> _passwordHasher = new PasswordHasher<User>();

        // Registracija novog korisnika
        public RegistrationResponseDto RegisterUser(RegistrationRequestDto dto)
        {
            // Validacija ulaznih polja
            if (string.IsNullOrWhiteSpace(dto.FirstName) || string.IsNullOrWhiteSpace(dto.LastName) ||
                string.IsNullOrWhiteSpace(dto.Username) || string.IsNullOrWhiteSpace(dto.Email) ||
                string.IsNullOrWhiteSpace(dto.Password) || string.IsNullOrWhiteSpace(dto.Role))
            {
                throw new ArgumentException("All fields are required.");
            }
            // Provjera jedinstvenosti emaila (i korisničkog imena ako je potrebno)
            if (UserRepository.GetUserByEmail(dto.Email) != null)
            {
                throw new ArgumentException("Email is already registered.");
            }
            if (UserRepository.GetUserByUsername(dto.Username) != null)
            {
                throw new ArgumentException("Username is already taken.");
            }

            // Kreiraj novog korisnika i hashiraj lozinku
            var newUser = new User
            {
                FirstName = dto.FirstName,
                LastName = dto.LastName,
                Username = dto.Username,
                Email = dto.Email,
                PasswordHash = _passwordHasher.HashPassword(null, dto.Password)  // hashiranje lozinke
            };
            // Spremi korisnika u "bazU" (in-memory lista)
            var savedUser = UserRepository.Add(newUser);

            // Pripremi odgovor s novim userId i username
            return new RegistrationResponseDto
            {
                UserId = savedUser.Id,
                Username = savedUser.Username
            };
        }

        // **NOVO**: Prijava postojećeg korisnika
        public LoginResponseDto LoginUser(LoginRequestDto dto)
        {
            // Validacija ulaznih polja
            if (string.IsNullOrWhiteSpace(dto.Username) || string.IsNullOrWhiteSpace(dto.Password))
            {
                throw new ArgumentException("Username and password are required.");
            }

            // Provjeri postoji li korisnik s danim korisničkim imenom
            var user = UserRepository.GetUserByUsername(dto.Username);
            if (user == null)
            {
                throw new ArgumentException("Invalid username or password.");
            }

            // Provjera lozinke pomoću PasswordHasher (usporedba hashirane lozinke)
            var verifyResult = _passwordHasher.VerifyHashedPassword(user, user.PasswordHash, dto.Password);
            if (verifyResult != PasswordVerificationResult.Success)
            {
                throw new ArgumentException("Invalid username or password.");
            }

            // Ako su kredencijali ispravni, vrati LoginResponseDto s podacima o korisniku
            return new LoginResponseDto
            {
                UserId = user.Id,
                Username = user.Username
            };
        }
    }
}
