using Microsoft.EntityFrameworkCore;

namespace AICareerBuddy_Entities.Entities
{
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
        public virtual DbSet<JobListing> JobListings { get; set; }
        public virtual DbSet<ResumeFileInfo> ResumeFileInfos { get; set; }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            if (!optionsBuilder.IsConfigured)
            {
                // Fallback za slučaj kad netko radi new AIR_projektContext()
                optionsBuilder.UseSqlServer(
                    "Server=tcp:infoguardians.database.windows.net,1433;Initial Catalog=AIR_projekt;User ID=admin123;Password=Infoguardians123;Encrypt=True;TrustServerCertificate=False;Connection Timeout=30;");
            }
        }


        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            // USER
            modelBuilder.Entity<User>(entity =>
            {
                entity.ToTable("User");

                entity.Property(e => e.Username).HasMaxLength(100);
                entity.Property(e => e.Password).HasMaxLength(512);
                entity.Property(e => e.Role).HasMaxLength(50);
                entity.Property(e => e.FirstName).HasMaxLength(50);
                entity.Property(e => e.LastName).HasMaxLength(50);
                entity.Property(e => e.Email).HasMaxLength(256);
            });

            // JOBLISTING
            modelBuilder.Entity<JobListing>(entity =>
            {
                entity.ToTable("JobListing");

                entity.Property(e => e.Name).HasMaxLength(50);
                entity.Property(e => e.Category).HasMaxLength(50);
                entity.Property(e => e.Location).HasMaxLength(500);
                entity.Property(e => e.Terms).HasColumnType("text");
                entity.Property(e => e.Description).HasColumnType("text");

                entity.HasOne(d => d.Employer)
                    .WithMany(p => p.JobListings)
                    .HasForeignKey(d => d.EmployerId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("FK_JobListing_User");
            });

            // RESUMEFILEINFO
            modelBuilder.Entity<ResumeFileInfo>(entity =>
            {
                entity.ToTable("ResumeFileInfo");

                entity.Property(e => e.Name).HasMaxLength(50);
                entity.Property(e => e.Extension).HasMaxLength(50);
                entity.Property(e => e.Path).HasColumnType("text");

                entity.HasOne(d => d.User)
                    .WithMany(p => p.ResumeFileInfos)
                    .HasForeignKey(d => d.UserId)
                    .OnDelete(DeleteBehavior.ClientSetNull)
                    .HasConstraintName("FK_ResumeFileInfo_User");
            });
        }
    }
}
