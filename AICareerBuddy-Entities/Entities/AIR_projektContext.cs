using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Microsoft.EntityFrameworkCore;

namespace AICareerBuddy_Entities.Entities
{
    /// <summary>
    /// EF Core DbContext za bazu AIR_projekt.
    /// Za sada mapira samo entitet User, ali se može proširiti (JobListing, ResumeFileInfo, itd.).
    /// </summary>
    public partial class AIR_projektContext : DbContext
    {
        public AIR_projektContext()
        {
        }

        public AIR_projektContext(DbContextOptions<AIR_projektContext> options)
            : base(options)
        {
        }

        public virtual DbSet<User> Users { get; set; }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            modelBuilder.Entity<User>(entity =>
            {
                entity.ToTable("User"); // <-- OVO JE ISPRAVNO PREMA TVOJEM SQL SERVERU

                entity.HasKey(e => e.Id);

                entity.Property(e => e.Id)
                    .ValueGeneratedOnAdd();

                entity.Property(e => e.FirstName)
                    .IsRequired()
                    .HasMaxLength(50);

                entity.Property(e => e.LastName)
                    .IsRequired()
                    .HasMaxLength(50);

                entity.Property(e => e.Username)
                    .IsRequired()
                    .HasMaxLength(100);

                entity.Property(e => e.Email)
                    .IsRequired()
                    .HasMaxLength(256);

                entity.Property(e => e.PasswordHash)
                    .IsRequired()
                    .HasMaxLength(512)
                    .HasColumnName("Password");


                entity.Property(e => e.Role)
                    .HasMaxLength(50);

                entity.Property(e => e.CreatedAt)
                    .HasColumnType("datetime");
            });
            OnModelCreatingPartial(modelBuilder);
        }

        partial void OnModelCreatingPartial(ModelBuilder modelBuilder);
    }
}
