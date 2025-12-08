using AICareerBuddy_BussinesLogic.Services;
using Microsoft.EntityFrameworkCore;
using AICareerBuddy_DataAccessLayer.Models;
using AICareerBuddy_BussinesLogicLayer.Interfaces;
using AICareerBuddy_BussinesLogic.Services;



var builder = WebApplication.CreateBuilder(args);
builder.Services.AddDbContext<AIR_projektContext>(options =>
    options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));

builder.Services.AddScoped<IAuthService, AuthService>();

builder.Configuration.AddUserSecrets<Program>();

// Add the RegistrationService for DI
builder.Services.AddScoped<AuthService>();


builder.Services.AddControllers();
// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

var app = builder.Build();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthorization();

app.MapControllers();

app.Run();