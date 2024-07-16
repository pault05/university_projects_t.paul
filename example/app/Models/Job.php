<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Job extends Model {
    use HasFactory;

    protected $table = 'job_listings';

    protected $guarded = [];


    public function employer()
    {
        return $this->belongsTo(Employer::class);
    }
}

/*
   public static function all():array
    {
         return [
            [
                'id' => 1,
                'title' => 'Director',
                'salary' => '$5000'
            ],
            [
                'id' => 2,
                'title' => 'Manager',
                'salary' => '$3000'
            ],
            [
                'id' => 3,
                'title' => 'Salahor',
                'salary' => '$1000'
            ]
        ];
    }

    public static function find(int $id):array
    {
       $job = Arr::first(static::all(), fn($job) => $job['id'] == $id);

       if($job == null){
           abort(404, 'Not Found');
       }
       else {return $job;}
    }

 */
