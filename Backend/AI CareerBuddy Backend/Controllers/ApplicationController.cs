using AICareerBuddy_BussinesLayer.Interfaces;
using AICareerBuddy_Entities.Entities;
using Microsoft.AspNetCore.Mvc;
using System.Linq;
using System.Threading.Tasks;
using System.Collections.Generic;
using System;

namespace AI_CareerBuddy_Backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class ApplicationController : ControllerBase
    {
        private readonly IApplicationService ApplicationService;
        private readonly IJobService JobService;

        public ApplicationController(IApplicationService applicationService, IJobService jobService)
        {
            ApplicationService = applicationService;
            JobService = jobService;
        }

        // GET: api/<ApplicationController>
        [HttpGet]
        public async Task<ActionResult<IEnumerable<JobApplication>>> Get()
        {
            var applications = await ApplicationService.GetApplications();
            if (applications != null) { return Ok(applications); }
            else { return NotFound(); }
        }

        [HttpGet("student")]
        public async Task<ActionResult<IEnumerable<object>>> GetByStudentId([FromQuery] int studentId)
        {
            var applications = await ApplicationService.GetApplicationsByStudentId(studentId);
            if (applications == null) return NotFound();

            var result = new List<object>();

            foreach (var app in applications)
            {
                var student = await JobService.GetStudentById(app.StudentId);
                var job = await JobService.GetJob(app.JobId);

                result.Add(new
                {
                    Id = app.Id,
                    StudentId = app.StudentId,
                    JobId = app.JobId,
                    EmployerId = app.EmployerId,
                    DateOfSubmission = app.DateOfSubmission?.ToString("yyyy-MM-dd"),
                    Status = app.Status,
                    ExpectedPay = app.ExpectedPay,
                    WorkExperience = app.WorkExperience,
                    Education = app.Education,
                    InterviewDate = app.InterviewDate?.ToString("yyyy-MM-dd"),
                    StudentName = student != null ? $"{student.FirstName} {student.LastName}" : null,
                    JobName = job?.Name
                });
            }

            return Ok(result);
        }

        [HttpGet("employer")]
        public async Task<ActionResult<IEnumerable<JobApplication>>> GetByEmployerId([FromQuery] int employerId)
        {
            var applications = await ApplicationService.GetApplicationsByEmployerId(employerId);
            if (applications == null) { return NotFound(); }

            var result = new List<object>();

            foreach (var app in applications)
            {
                var student = await JobService.GetStudentById(app.StudentId);
                var job = await JobService.GetJob(app.JobId);

                result.Add(new
                {
                    Id = app.Id,
                    StudentId = app.StudentId,
                    JobId = app.JobId,
                    EmployerId = app.EmployerId,
                    DateOfSubmission = app.DateOfSubmission?.ToString("yyyy-MM-dd"),
                    Status = app.Status,
                    ExpectedPay = app.ExpectedPay,
                    WorkExperience = app.WorkExperience,
                    Education = app.Education,
                    InterviewDate = app.InterviewDate?.ToString("yyyy-MM-dd"),
                    StudentName = student != null ? $"{student.FirstName} {student.LastName}" : null,
                    JobName = job?.Name
                });
            }

            return Ok(result);
        }

        [HttpGet("job")]
        public async Task<ActionResult<IEnumerable<JobApplication>>> GetByJobId([FromQuery] int jobId)
        {
            var applications = await ApplicationService.GetApplicationsByJobId(jobId);
            if (applications != null) { return Ok(applications); }
            else { return NotFound(); }
        }

        // GET api/<ApplicationController>/5
        [HttpGet("id")]
        public async Task<ActionResult<JobApplication>> Get([FromQuery] int id)
        {
            var application = await ApplicationService.GetApplicationById(id);
            if (application != null) { return Ok(application); }
            else { return NotFound(); }
        }

        // POST api/<ApplicationController>
        [HttpPost]
        public async Task<ActionResult<JobApplication>> Post([FromBody] JobApplication application)
        {
            var isCreated = await ApplicationService.PostApplication(application);
            if (isCreated) { return Created(); }
            else { return BadRequest(); }
        }

        // PUT api/<ApplicationController>/5
        [HttpPut("{id}")]
        public async Task<ActionResult<JobApplication>> Put(int id, [FromBody] JobApplication application)
        {
            if (application == null) { return BadRequest("PReLoše"); }
            if (application.Id != id) { return BadRequest("Loše"); }
            var updated = await ApplicationService.PutApplication(application);
            if (updated) { return Ok(new API_Response { success = true, message = "Uspješna promjena" }); }
            else { return NotFound(); }
        }

        // DELETE api/<ApplicationController>/5
        [HttpDelete("{id}")]
        public async Task<ActionResult<JobApplication>> Delete(int id)
        {
            var deleted = await ApplicationService.DeleteApplication(id);
            if (deleted) { return NoContent(); }
            else { return NotFound(); }
        }
    }
}
