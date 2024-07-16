@props(['name'])
@error($name)
    <p class="text-xs text-red-500 italic font-bold"> {{$message}} </p>
@enderror
