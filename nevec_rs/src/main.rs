mod cli;

fn main() {
    let (filename, options) = cli::parse_args();

    println!("Compiling {}", filename);
    println!("Doing optimizations: {}", &options.do_opt);
}
