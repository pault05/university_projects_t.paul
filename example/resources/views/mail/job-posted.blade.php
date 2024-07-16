<h2>
    {{$job->title}}
</h2>

<p>
    Congrats! Job posted!
</p>

<p>
    <a href="{{url('/jobs/' . $job->id)}}"> View your job.</a>
</p>
