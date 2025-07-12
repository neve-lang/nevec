<p align=center>
  <img src="images/folipa.svg" alt="Folipa, Neve’s logo (look, she’s waving with her upper left petal!!)">
</p>

# The Neve Programming Language

Neve is a type-safe, **dreamy** programming language that 
makes your code predictable and fun to work with.

## Development

I’ve recently decided to ditch the idea of implementing everything at once—I was
genuinely overwhelmed.  It’s completely true that I’ve made a lot of progress 
with the IR and the optimization stage, but it just constantly felt like things
kept getting more complex, and it was overwhelming to know that I still had so 
many things to implement.

And so I’ve decided to do things differently—instead of Neve code running
on the VM *right away*, I’ll first **transpile to Haskell** as a **stepping
stone**, so Neve code will compile to a Haskell project.  Things will stay 
that way, until I finally decide to self-host Neve—this is where I’ll
finally implement the VM and LLVM support, as Neve’s design will be much more
stable once that happens.