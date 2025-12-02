using AICareerBuddy_BussinesLayer.Interfaces;
using AICareerBuddy_BussinesLayer.Services;
using AICareerBuddy_BussinesLogic.Services;
using AICareerBuddy_DataAccessLayer.Repositories;
using AICareerBuddy_Entities.Entities;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

builder.Configuration.AddUserSecrets<Program>();


//builder.Services.AddDbContext<AIR_projektContext>(options =>
 //   options.UseSqlServer(builder.Configuration.GetConnectionString("DefaultConnection")));

//builder.Services.AddScoped<JobRepository>();

builder.Services.AddScoped<IResumeService, ResumeService>();
builder.Services.AddScoped<IJobService, JobService>();


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
