<?php

namespace App\Http\Controllers;

use App\Mail\JobPosted;
use App\Models\Job;
use Illuminate\Support\Facades\Gate;
use Illuminate\Support\Facades\Mail;

class  JobController extends Controller
{
    public function index(){
        $jobs = Job::with('employer')->latest()->simplePaginate(10);

        return view('jobs.index', [
        'jobs' => $jobs
        ]);
    }

    public function create(){
        return view('jobs.create');
    }

    public function show(Job $job){
        return view('jobs.show', ['job' => $job]);
    }

    public function store(){
        //validation
        request()->validate([
            'title' => ['required', 'min:3'] ,
            'salary' => ['required'],
        ]);

       $job = Job::create([
            'title' => request('title'),
            'salary' => request('salary'),
            'employer_id' => 1
        ]);

        Mail::to($job->employer->user)->queue(new JobPosted($job));


        return redirect('/jobs');
    }

    public function edit(Job $job){

//       if(Auth::user()->cannot('edit-job', $job))
//        {
//            dd('You do not have permission to edit this job');
//        }
//        Gate::authorize('edit-job', $job);         //permite editarea jobului de catre userul care l-a creat

        return view('jobs.edit', ['job' => $job]);
    }

    public function update(Job $job){
        Gate::authorize('edit', $job);
        // validate
        request()->validate([
            'title' => ['required', 'min:3'] ,
            'salary' => ['required'],
        ]);

        $job->update([
            'title' => request('title'),
            'salary' => request('salary')
        ]);

        // persist
        //redirect to the job page
        return redirect('/jobs/' . $job->id);
    }

    public function destroy(Job $job){
        Gate::authorize('edit', $job);
        $job->delete();
        return redirect('/jobs/');
    }
}

// in web

//Route::controller(JobController::class)->group(function () {
//
//    //index
//    Route::get('/jobs','index');
//
//    //create a job
//    Route::get('/jobs/create','create');
//
//    // show a job
//    Route::get('/jobs/{job}', 'show');
//
//    // store a job
//    Route::post('/jobs','store');
//
//    // edit a job
//    Route::get('/jobs/{job}/edit', 'edit');
//
//    //update a job
//    Route::patch('/jobs/{job}',  'update');
//
//    // destroy a job
//    Route::delete('/jobs/{job}', 'destroy');
//});
