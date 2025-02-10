use clap::Parser;

#[derive(Parser)]
#[command(name = "nevec")]
#[command(version = "0.1")]
#[command(about = "The Neve compiler", long_about = None)]
pub struct Cli {
    #[arg(long)]
    pub no_opt: bool,

    #[arg(required = true)]
    pub input: String,
}
