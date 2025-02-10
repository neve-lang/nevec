use clap::Parser;

use cli::Cli;

mod cli;

fn main() {
    let cli = Cli::parse();

    let filename = cli.input;
    let do_opt = cli.no_opt;

    println!("Compiling {}!", filename);
    println!("Optimizations enabled: {}.", do_opt);
}
