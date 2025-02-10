use clap::Parser;

#[derive(Parser)]
#[command(name = "nevec")]
#[command(version = "0.1")]
#[command(about = "The Neve compiler", long_about = None)]
pub struct Cli {
    /// Disables all non-memory related optimizations
    #[arg(long)]
    pub no_opt: bool,

    /// The Neve file to compile
    #[arg(required = true)]
    pub input: String,
}

pub struct CliOptions {
    pub do_opt: bool,
}

impl CliOptions {
    fn from(cli: &Cli) -> CliOptions {
        CliOptions {
            do_opt: !cli.no_opt,
        }
    }
}

pub fn parse_args() -> (String, CliOptions) {
    let cli = Cli::parse();
    let filename = cli.input.clone();

    (filename, CliOptions::from(&cli))
}
