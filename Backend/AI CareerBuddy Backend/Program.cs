var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();


builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAll", policy =>
    {
        policy
            .AllowAnyOrigin()   // dopušta zahtjeve s bilo koje domene (npr. 10.0.2.2)
            .AllowAnyMethod()   // dopušta GET, POST, PUT, DELETE itd.
            .AllowAnyHeader();  // dopušta custom headere (npr. Authorization)
    });
});


var app = builder.Build();


if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}


app.UseCors("AllowAll");      // mora iæi prije UseAuthorization
app.UseHttpsRedirection();
app.UseAuthorization();


app.MapControllers();

app.Run();
